import G6_iris_recognition
#
# train_database_path = "Input_database/"
# train_encoding_model_path = "encodingModel/irisEncodings.pickle"
# G6_iris_recognition.iris_model_train(train_database_path, train_encoding_model_path)

test_encoding_model_path = "encodingModel/irisEncodings.pickle"
real_time_image_path1 = "realPerson/casia1.png"
iris_name1 = G6_iris_recognition.iris_model_test(test_encoding_model_path,real_time_image_path1)
print('name=')
print({iris_name1})
#iris_name                  ===>  it returns predicted person name if image matches with trained image model person image & if not then it returns name as unmatch.

# real_time_image_path2 = "realPerson/Human_Eye.png"
# iris_name2 = G6_iris_recognition.iris_model_test(test_encoding_model_path,real_time_image_path2)
# print('name=')
# print({iris_name2})
#
#
# real_time_image_path3 = "realPerson/8.jpg"
# iris_name3 = G6_iris_recognition.iris_model_test(test_encoding_model_path,real_time_image_path3)
# print('name=')
# print({iris_name3})