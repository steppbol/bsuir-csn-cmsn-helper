import numpy as np
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression
from functools import reduce
from collections import Counter


def parse_file(file_name, first_attribute_column, second_attribute_column):
    first_attribute = []
    second_attribute = []
    with open(file_name, 'r') as file:
        for line in file:
            parsed_data = line.split(',')
            if parsed_data[first_attribute_column] != '?' and parsed_data[second_attribute_column] != '?':
                first_attribute.append(float(parsed_data[first_attribute_column]))
                second_attribute.append(float(parsed_data[second_attribute_column]))
    return first_attribute, second_attribute, len(first_attribute)


def normalize_weight_map(weight_map, data_size):
    for key, value in weight_map.items():
        weight_map[key] = value / data_size
    return weight_map


def expected_value(random_variable):
    # computing weights of random variable
    random_variable_weights_map = normalize_weight_map(Counter(random_variable), len(random_variable))

    # decompose values and their weights
    random_variable_values = list(random_variable_weights_map.keys())
    random_variable_weights = list(random_variable_weights_map.values())

    return np.average(random_variable_values, weights=random_variable_weights)


def expected_value_avg(random_variable):
    return reduce((lambda x, y: x + y), random_variable) / len(random_variable)


def sample_variance(random_variable):
    return expected_value(np.square(random_variable)) - pow(expected_value(random_variable), 2)


def unbiased_sample_variance(random_variable):
    n = len(random_variable)
    return sample_variance(random_variable) * n / (n - 1)


def main():
    wind_speed, temperature, parsed_data_size = parse_file('eighthr.txt', 26, 52)

    # converting string lists to numeric lists
    wind_speed = list(map(float, wind_speed))
    temperature = list(map(float, temperature))

    # converting data to 2-dimensional arrays in order to work with them
    # because LinearRegression() works with tables represented by matrices
    temperature_two_d_array = np.array(list(zip(temperature)))
    wind_speed_two_d_array = np.array(list(zip(wind_speed)))

    # setting up the regression model
    regression = LinearRegression()
    # Train the model using the given sets
    regression.fit(wind_speed_two_d_array, temperature_two_d_array)
    # Make predictions using the testing set
    temperature_pred = regression.predict(wind_speed_two_d_array)

    print('Expected value of average wind speed = ', expected_value(wind_speed))
    print('Expected value of average temperature = ', expected_value(temperature))

    print('Expected value of average wind speed through average = ', expected_value_avg(wind_speed))
    print('Expected value of average temperature through average = ', expected_value_avg(temperature))

    print('Variance of average wind speed = ', sample_variance(wind_speed))
    print('Variance of average temperature = ', sample_variance(temperature))

    print('Unbiased variance of average wind speed = ', unbiased_sample_variance(wind_speed))
    print('Unbiased variance of average temperature = ', unbiased_sample_variance(temperature))

    print('NUMPY Variance of average wind speed = ', np.var(wind_speed))
    print('NUMPY Variance of average temperature = ', np.var(temperature))

    print('Standard deviation of average wind speed = ', np.sqrt(sample_variance(wind_speed)))
    print('Standard deviation of average temperature = ', np.sqrt(sample_variance(temperature)))

    print('Correlation coefficient = ', np.corrcoef(wind_speed, temperature)[0, 1])

    figure = plt.figure()
    ax = figure.add_subplot(111)
    ax.set_xlabel('Average wind speed')
    ax.set_ylabel('Average temperature')
    plt.plot(wind_speed, temperature, color='b', marker='2', linestyle='')

    # the way to draw regression model manually
    # plt.plot(wind_speed, regression.intercept_ + regression.coef_[0] * wind_speed, color='r')
    # or with the help of built-in prediction mechanism
    plt.plot(wind_speed_two_d_array, temperature_pred, color='r')

    plt.show()


if __name__ == '__main__':
    main()
