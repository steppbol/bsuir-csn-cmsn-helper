from PIL import Image
import matplotlib.pyplot as plt
from scipy.special import erf
from scipy.stats.distributions import chi2
import numpy as np


def compute_mode(intervals, frequencies):
    max_frequencies = max(frequencies)
    index = list(frequencies).index(max_frequencies)

    if index > 0:
        prev_frequency = frequencies[index - 1]
    else:
        prev_frequency = 0

    if index < len(frequencies) - 1:
        next_frequency = frequencies[index + 1]
    else:
        next_frequency = 0

    interval_size = intervals[1] - intervals[0]

    if index > 0:
        interval_lower_border = intervals[index - 1]
    else:
        interval_lower_border = 0

    return \
        interval_lower_border \
        + interval_size \
        * (max_frequencies - prev_frequency) \
        / ((max_frequencies - prev_frequency) + (max_frequencies - next_frequency))


def compute_median(intervals, frequencies):
    max_frequencies = max(frequencies)
    index = list(frequencies).index(max_frequencies)

    interval_size = intervals[1] - intervals[0]

    if index > 0:
        interval_lower_border = intervals[index - 1]
    else:
        interval_lower_border = 0

    acc = sum(frequencies)
    prev_acc = sum(frequencies[:index])

    return \
        interval_lower_border \
        + interval_size * (acc / 2 - prev_acc) / max_frequencies


def generate_theoretical_probability(intervals, mean, std):
    result = []
    for i in range(len(intervals) - 1):
        result.append(erf((intervals[i + 1] - mean) / std) - erf((intervals[i] - mean) / std))

    print(sum(result))

    return result


def is_distributed_normally(frequencies, intervals, mean, std, alpha):
    n = len(intervals) - 1
    total_occurrence = sum(frequencies)
    theoretical_probabilities = \
        generate_theoretical_probability(intervals, mean, std)

    # for i in range(n):
    #    print(frequencies[i], '::', theoretical_probabilities[i])

    chi_squared = 0
    for i in range(n):
        chi_squared += (frequencies[i] - total_occurrence * theoretical_probabilities[i]) ** 2 \
                       / (total_occurrence * theoretical_probabilities[i])

    quantile = chi2.ppf(alpha, df=(total_occurrence - 1))

    print('Chi squared: {}; Quantile: {}'.format(chi_squared, quantile))

    return chi_squared < quantile


def main():
    path_to_first_image = 'firepower.jpg'
    path_to_second_image = 'fandango.jpg'

    first_image = Image.open(path_to_first_image).convert('L')
    second_image = Image.open(path_to_second_image).convert('L')

    first_width, first_height = first_image.size
    second_width, second_height = second_image.size

    size = min(first_width, second_width), min(first_height, second_height)

    first_image = first_image.resize(size, Image.BICUBIC)
    second_image = second_image.resize(size, Image.BICUBIC)

    first_image_pixels = list(first_image.getdata())
    second_image_pixels = list(second_image.getdata())

    bins = list(range(0, 255, 5))
    num_bins = int(255 / 5)
    bins_range = 0, 255

    first_bins_values, _, _ = \
        plt.hist(first_image_pixels, num_bins, bins_range, facecolor='red', edgecolor='black', linewidth=1)
    plt.show()
    second_bins_values, _, _ = \
        plt.hist(second_image_pixels, num_bins, bins_range, facecolor='red', edgecolor='black', linewidth=1)
    plt.show()

    print('First histogram\'s average value: ', np.mean(first_bins_values))
    print('Second histogram\'s average value: ', np.mean(second_bins_values))
    print('\n')
    print('First histogram\'s standard deviation: ', np.std(first_bins_values))
    print('Second histogram\'s average deviation: ', np.std(second_bins_values))
    print('\n')
    print('First histogram\'s mode: ', compute_mode(bins, first_bins_values))
    print('Second histogram\'s mode: ', compute_mode(bins, second_bins_values))
    print('\n')
    print('First histogram\'s median: ', compute_median(bins, first_bins_values))
    print('Second histogram\'s median: ', compute_median(bins, second_bins_values))
    print('\n')
    print('Images\' correlation coefficient: ', np.corrcoef(first_image_pixels, second_image_pixels)[0, 1])
    print('Histograms\' correlation coefficient: ', np.corrcoef(first_bins_values, second_bins_values)[0, 1])

    print('First image has normal distribution: ', is_distributed_normally(
        first_bins_values,
        bins,
        np.mean(first_image_pixels),
        np.std(first_image_pixels),
        0.05
    ))

    print('Second image has normal distribution: ', is_distributed_normally(
        second_bins_values,
        bins,
        np.mean(second_image_pixels),
        np.std(second_image_pixels),
        0.05
    ))

    first_image.show()
    second_image.show()

    first_image.close()
    second_image.close()


if __name__ == '__main__':
    main()
