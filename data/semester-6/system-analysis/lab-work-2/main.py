import numpy as np
import pandas as pd
from functools import reduce
from scipy.stats.distributions import chi2
from scipy.stats import t


def expected_value(random_variable):
    return reduce((lambda x, y: x + y), random_variable) / len(random_variable)


def sample_variance(random_variable):
    return expected_value(np.square(random_variable)) - pow(expected_value(random_variable), 2)


def compute_chi2(random_variable, alpha):
    coefficient = 1 if alpha > 0.5 else -1
    denominator = (1 - alpha) if coefficient == 1 else alpha
    d = coefficient * (2.0637 * (np.log(1 / denominator) - 0.16) ** 0.4274 - 1.5774)
    A = d * np.sqrt(2)
    B = 2 / 3 * (d ** 2 - 1)
    C = d * (d ** 2 - 7) / 9 * np.sqrt(2)
    D = -(6 * d ** 4 + 14 * d ** 2 - 32) / 405
    E = d * (9 * d ** 4 + 256 * d ** 2 - 433) / (4860 * np.sqrt(2))
    n = len(random_variable) - 1

    return n + A * np.sqrt(n) + B + C / np.sqrt(n) + D / n + E / (n * np.sqrt(n))


def compute_var_interval_border(random_variable, alpha):
    length = len(random_variable)
    quantile_high = compute_chi2(random_variable, alpha / 2)
    quantile_low = chi2.ppf(1 - alpha / 2, df=(length - 1))
    variance = unbiased_sample_variance(random_variable)

    low_border = variance * (length - 1) / quantile_low
    high_border = variance * (length - 1) / quantile_high

    return low_border, high_border


def unbiased_sample_variance(random_variable):
    n = len(random_variable)

    return sample_variance(random_variable) * n / (n - 1)


def expected_value_confidence_interval(random_variable, alpha):
    coefficient = np.abs(t.ppf(alpha / 2, df=(len(random_variable) - 1)))

    return coefficient * np.sqrt(unbiased_sample_variance(random_variable) / len(random_variable))


def are_expected_values_equal_known_var(random_variable1, random_variable2, erf_coeff):
    expected_value1 = expected_value(random_variable1)
    expected_value2 = expected_value(random_variable2)
    variance1 = unbiased_sample_variance(random_variable1)
    variance2 = unbiased_sample_variance(random_variable2)
    length1 = len(random_variable1)
    length2 = len(random_variable2)

    return erf_coeff > np.abs(expected_value1 - expected_value2) / np.sqrt((variance1 / length1) + (variance2 / length2))


def are_expected_values_equal_unknown_var(random_variable1, random_variable2, alpha):
    expected_value1 = expected_value(random_variable1)
    expected_value2 = expected_value(random_variable2)
    unbiased_var1 = unbiased_sample_variance(random_variable1)
    unbiased_var2 = unbiased_sample_variance(random_variable2)
    length1 = len(random_variable1)
    length2 = len(random_variable2)

    estimate = np.abs(expected_value1 - expected_value2) /\
        (np.sqrt((length1 - 1) * unbiased_var1 + (length2 - 1) * unbiased_var2)) *\
            np.sqrt(((length1 * length2) * (length1 + length2 - 2)) / (length1 + length2))

    student_coeff = np.abs(t.ppf(alpha / 2, df=(length1 + length2 - 2)))

    return student_coeff > estimate


def main():
    with open('eighthr.txt', 'r') as file:
        df = pd.read_csv(file, na_values=['?'])
        df = df.dropna()
        wind_speed = list(map(float, df.iloc[:, 26]))
        temperature = list(map(float, df.iloc[:, 52]))

    alpha = 0.05

    wind_speed_expected = expected_value(wind_speed)
    temperature_expected = expected_value(temperature)

    wind_speed_sample_var = sample_variance(wind_speed)
    temperature_sample_var = sample_variance(temperature)

    wind_speed_unbiased_var = unbiased_sample_variance(wind_speed)
    temperature_unbiased_var = unbiased_sample_variance(temperature)

    wind_speed_deviation = np.sqrt(sample_variance(wind_speed))
    temperature_deviation = np.sqrt(sample_variance(temperature))

    print('Expected value of average wind speed = ', wind_speed_expected)
    print('Expected value of average temperature = ', temperature_expected)

    print('Variance of average wind speed = ', wind_speed_sample_var)
    print('Variance of average temperature = ', temperature_sample_var)

    print('Unbiased variance of average wind speed = ', wind_speed_unbiased_var)
    print('Unbiased variance of average temperature = ', temperature_unbiased_var)

    print('Standard deviation of average wind speed = ', wind_speed_deviation)
    print('Standard deviation of average temperature = ', temperature_deviation)

    # Expected values' confidence intervals computations

    print('Confidence interval of average wind\'s speed expected value: {} < E(x) < {}'.format(
        wind_speed_expected - expected_value_confidence_interval(wind_speed, alpha),
        wind_speed_expected + expected_value_confidence_interval(wind_speed, alpha)
    ))

    print('Confidence interval of average temperature\'s expected value: {} < E(x) < {}'.format(
        temperature_expected - expected_value_confidence_interval(temperature, alpha),
        temperature_expected + expected_value_confidence_interval(temperature, alpha)
    ))

    # Variances' confidence intervals computations

    low_wind_border, high_wind_border = compute_var_interval_border(wind_speed, alpha)
    print('Confidence interval of average temperature\'s variance: {} < sigma^2 < {}'.format(
        low_wind_border,
        high_wind_border
    ))

    low_temperature_border, high_temperature_border = compute_var_interval_border(temperature, alpha)
    print('Confidence interval of average temperature\'s variance: {} < sigma^2 < {}'.format(
        low_temperature_border,
        high_temperature_border
    ))

    # Hypothesises' checks

    # 1.65 - Laplace's function's arg with alpha equal to 0.05
    print('Is hypothesis that E(wind_speed) = E(temperature) with known variance correct:',
          are_expected_values_equal_known_var(wind_speed, temperature, 1.65))

    print('Is hypothesis that E(wind_speed) = E(temperature) with known variance correct:',
          are_expected_values_equal_unknown_var(wind_speed, temperature, alpha))


if __name__ == '__main__':
    main()
