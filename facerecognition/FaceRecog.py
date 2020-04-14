import mysql.connector
#flask for rest API
import flask
# importing face recognition package
import face_recognition
# import the os package
import os
# import cv2 for identifying face
import cv2

from mysql.connector import connect

def write_file(data, filename):
    # Convert binary data to proper format and write it on Hard Disk
    with open(filename, 'wb') as file:
        file.write(data)


def readBLOB(user_id, photo):
    print("Reading BLOB data from MFA_IMAGE_STORE_READ table")

    try:
        connection = mysql.connector.connect(host='ec2-13-127-239-253.ap-south-1.compute.amazonaws.com',
                                         database='ltimfadb',
                                             user='ltimfa',
                                             password='Simple@2020')

        cursor = connection.cursor()
        sql_fetch_blob_query = """SELECT * from MFA_IMAGE_STORE_READ where user_id = %s"""

        cursor.execute(sql_fetch_blob_query, (user_id,))
        record = cursor.fetchall()
        for row in record:
            print("Id = ", row[0], )
            print("Name = ", row[1])
            image = row[2]
            print("Storing employee image on disk \n")
            write_file(image, photo)

    except mysql.connector.Error as error:
        print("Failed to read BLOB data from MySQL table {}".format(error))

    finally:
        if (connection.is_connected()):
            cursor.close()
            connection.close()
            print("MySQL connection is closed")



def recognize_face (User_ID) :

    KNOWN_FACES_DIR = 'known_faces'
    NAME_OF_USER = User_ID  # input from API
    UNKNOWN_FACES_DIR = 'unknown_faces'
    TOLERANCE = 0.6
    FRAME_THICKNESS = 3
    FONT_THICKNESS = 2
    MODEL = 'hog'  # default: 'hog', other one can be 'cnn' - CUDA accelerated (if available) deep-learning pretrained model


    # Returns (R, G, B) from name
    def name_to_color(name):
        # Take 3 first letters, tolower()
        # lowercased character ord() value rage is 97 to 122, substract 97, multiply by 8
        color = [(ord(c.lower())-97)*8 for c in name[:3]]
        return color


    print('Loading known faces...')
    known_faces = []  # List of Faces
    known_names = []  # List of names

    # We organize known faces as subfolders of KNOWN_FACES_DIR
    # Each subfolder's name becomes our label (name)

    for name in os.listdir(KNOWN_FACES_DIR):
        # Next we load every file of faces of known person
        print(name)
        if name == NAME_OF_USER :
            for filename in os.listdir(f'{KNOWN_FACES_DIR}/{name}'):
                # Load an image
                image = face_recognition.load_image_file(f'{KNOWN_FACES_DIR}/{name}/{filename}')

                # Get 128-dimension face encoding
                # Always returns a list of found faces, for this purpose we take first face only (assuming one face per image as you can't be twice on one image)
                encoding = face_recognition.face_encodings(image)[0]

                # Append encodings and name
                known_faces.append(encoding)
                known_names.append(name)

    print('Processing unknown faces...')
    # Now let's loop over a folder of faces we want to label
    for filename in os.listdir(UNKNOWN_FACES_DIR):
        # Load image
        print(f'Filename {filename}', end='')
        image = face_recognition.load_image_file(f'{UNKNOWN_FACES_DIR}/{filename}')
        # This time we first grab face locations - we'll need them to draw boxes
        locations = face_recognition.face_locations(image, model=MODEL) # machine learning

        # Now since we know locations, we can pass them to face_encodings as second argument
        # Without that it will search for faces once again slowing down whole process
        encodings = face_recognition.face_encodings(image, locations)

        # We passed our image through face_locations and face_encodings, so we can modify it
        # First we need to convert it from RGB to BGR as we are going to work with cv2
        image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)

        # But this time we assume that there might be more faces in an image - we can find faces of different people
        print(f', found {len(encodings)} face(s)')
        for face_encoding, face_location in zip(encodings, locations):

            # We use compare_faces (but might use face_distance as well)
            # Returns array of True/False values in order of passed known_faces
            results = face_recognition.compare_faces(known_faces, face_encoding, TOLERANCE)
            # Since order is being preserved, we check if any face was found then grab index
            # then label (name) of first matching known face withing a tolerance
            match = None
            if True in results:  # If at least one is true, get a name of first of found labels
                match = known_names[results.index(True)]
                print(f' - {match} from {results}')

                # Each location contains positions in order: top, right, bottom, left
                #top_left = (face_location[3], face_location[0])
               # bottom_right = (face_location[1], face_location[2])

                # Get color by name using our fancy function
                #color = name_to_color(match)

                # Paint frame
               # cv2.rectangle(image, top_left, bottom_right, color, FRAME_THICKNESS)

                # Now we need smaller, filled grame below for a name
                # This time we use bottom in both corners - to start from bottom and move 50 pixels down
              #  top_left = (face_location[3], face_location[2])
               # bottom_right = (face_location[1], face_location[2] + 22)

                # Paint frame
                #cv2.rectangle(image, top_left, bottom_right, color, cv2.FILLED)

                # Wite a name
                #cv2.putText(image, match, (face_location[3] + 10, face_location[2] + 15), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (200, 200, 200), FONT_THICKNESS)
                return 1

        # Show image
        #cv2.imshow(filename, image)
        #cv2.waitKey(0)
        # cv2.destroyWindow(filename)




# Using flask to make an api
# import necessary libraries and functions
from flask import Flask, jsonify, request
app = Flask(__name__)

# on the terminal type: curl http://127.0.0.1:5000/
# returns hello world when we use GET.
# returns the data that we send when we use POST.
@app.route('/facerec/<int:num>', methods = ['GET', 'POST'])
def faceverify(num):
    if (request.method == 'GET'):
        #data = "hello world"
        readBLOB(num, "C:\\Users\\Pawan Pandey\\PycharmProjects\\FaceRec1\\unknown_faces\\ManojTest5.jpg")
        response = recognize_face (str(num))
        # print (int)
    return jsonify({'userid':num, 'facereco': response})
app.run()


