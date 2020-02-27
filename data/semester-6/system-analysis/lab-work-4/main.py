import spacy
import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
from scipy.cluster.vq import kmeans
from collections import Counter


def form_unique_text_lemmas(file_name, nlp):
    min_len = 2
    with open(file_name, 'r', encoding='utf-16') as file:
        text = file.read()
        doc = nlp(text)

        valid_tokens = []

        for token in doc:
            if token.is_alpha and not token.is_stop and len(token.lemma_) > min_len:
                valid_tokens.append(token.lower_)

        unique_lemmas_dict = dict(zip(
            Counter(valid_tokens).keys(),
            Counter(valid_tokens).values()
        ))

    return unique_lemmas_dict


def get_bag_of_words(file_names, nlp):
    unique_lemmas_dictionaries = []

    for file_name in file_names:
        unique_lemmas_dictionaries.append(form_unique_text_lemmas(file_name, nlp))

    bag_of_words = {}
    for dictionary in unique_lemmas_dictionaries:
        for key in dictionary.keys():
            if bag_of_words.get(key) is None:
                bag_of_words[key] = dictionary[key]
            else:
                bag_of_words[key] += dictionary[key]

    return bag_of_words


def filter_bags_of_words(bags_of_words):
    filtered_bags_of_words = bags_of_words.copy()

    for bag_of_words in filtered_bags_of_words:
        for key in bag_of_words:
            for another_bag_of_words in filtered_bags_of_words:
                if key in another_bag_of_words:
                    if bag_of_words[key] >= another_bag_of_words[key] and bag_of_words != another_bag_of_words:
                        del another_bag_of_words[key]

    return filtered_bags_of_words


def form_thesauruses(bags_of_words, min_occurence):
    filtered_bags = filter_bags_of_words(bags_of_words)

    thesauruses = []
    for bag in filtered_bags:
        thesaurus = {}
        for key in bag:
            if bag[key] >= min_occurence:
                thesaurus[key] = bag[key]
        thesauruses.append(thesaurus)

    return thesauruses


def vectorize_files(thesauruses, file_names, nlp):
    vectors = []
    thesauruses_amount = len(thesauruses)

    for file_name in file_names:
        vector = [.0] * thesauruses_amount

        with open(file_name, 'r', encoding='utf-16') as file:
            text = file.read()
            doc = nlp(text)

            for token in doc:
                for i in range(thesauruses_amount):
                    if token.lower_ in thesauruses[i]:
                        vector[i] += 1.0

        vectors.append(vector)
        print(vector)

    return vectors


def plot_results(vectors, texts_per_category, centroids):
    ox = [vector[0] for vector in vectors]
    oy = [vector[1] for vector in vectors]
    oz = [vector[2] for vector in vectors]
    colors = ['r', 'g', 'b']
    n = int(len(vectors) / texts_per_category)

    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')

    for i in range(n):
        offset = i * texts_per_category

        temp_x = ox[offset: offset + texts_per_category]
        temp_y = oy[offset: offset + texts_per_category]
        temp_z = oz[offset: offset + texts_per_category]

        ax.scatter(temp_x, temp_y, temp_z, c=colors[i], marker='o')
        ax.scatter(
            centroids[i][0],
            centroids[i][1],
            centroids[i][2],
            c='k', marker='x'
        )

    ax.set_xlabel('Maths')
    ax.set_ylabel('Chemistry')
    ax.set_zlabel('Medicine')
    plt.show()


def main():
    file_names = [
        'maths_1.txt',
        'maths_2.txt',
        'maths_3.txt',
        'medicine_1.txt',
        'medicine_2.txt',
        'medicine_3.txt',
        'chemistry_1.txt',
        'chemistry_2.txt',
        'chemistry_3.txt'
    ]

    training_texts = ['training/' + file_name for file_name in file_names]
    test_texts = ['test/' + file_name for file_name in file_names]

    min_occurence = 3
    nlp = spacy.load('en_core_web_sm')

    maths_bag_of_words = get_bag_of_words(training_texts[:3], nlp)
    chemistry_bag_of_words = get_bag_of_words(training_texts[3:6], nlp)
    medicine_bag_of_words = get_bag_of_words(training_texts[6:9], nlp)

    thesauruses = form_thesauruses(
        [
            maths_bag_of_words,
            chemistry_bag_of_words,
            medicine_bag_of_words
        ],
        min_occurence
    )

    vectors = vectorize_files(thesauruses, test_texts, nlp)
    centroids, _ = kmeans(np.array(vectors), 3)
    plot_results(vectors, 3, centroids)


if __name__ == '__main__':
    main()
