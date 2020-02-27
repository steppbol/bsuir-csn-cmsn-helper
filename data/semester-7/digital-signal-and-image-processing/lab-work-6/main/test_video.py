import numpy as np
import cv2
import os
import shutil
from skimage.measure import compare_ssim

dirname = 'images'
video_name = 'vid2.avi'
roi_window_name = 'ROI selection'


def main():
    if os.path.exists(dirname):
        shutil.rmtree(dirname)
    os.mkdir(dirname)

    i = 0
    previous_image = None
    is_writable = False

    cap = cv2.VideoCapture(video_name)
    ret, frame = cap.read()

    roi = cv2.selectROI(roi_window_name, frame)
    cv2.destroyWindow(roi_window_name)

    while cap.isOpened():
        i += 1
        ret, frame = cap.read()

        if not ret:
            break

        crop_image = frame[int(roi[1]):int(roi[1] + roi[3]), int(roi[0]):int(roi[0] + roi[2])]
        hsv = cv2.cvtColor(crop_image, cv2.COLOR_BGR2HSV)

        lower_red = np.array([0, 120, 70])
        upper_red = np.array([10, 255, 255])
        mask1 = cv2.inRange(hsv, lower_red, upper_red)

        lower_red = np.array([170, 120, 70])
        upper_red = np.array([180, 255, 255])
        mask2 = cv2.inRange(hsv, lower_red, upper_red)
        mask1 = mask1 + mask2
        mask1 = cv2.morphologyEx(mask1, cv2.MORPH_OPEN, np.ones((3, 3), np.uint8), iterations=2)
        mask1 = cv2.dilate(mask1, np.ones((3, 3), np.uint8), iterations=1)

        nb_components, output, stats, centroids = cv2.connectedComponentsWithStats(mask1, connectivity=8)
        sizes = stats[1:, -1]

        if nb_components - 1 != 0:
            max_component_index = np.argmax(sizes) + 1
            image_with_max_component = np.zeros(output.shape)

            max_component_stat = stats[max_component_index]
            image_with_max_component[output == max_component_index] = 255

            if max_component_stat[4] > 20000 and (max_component_stat[2] / max_component_stat[3]) > 1 \
                    and max_component_stat[0] != 0 and max_component_stat[1] != 0 \
                    and max_component_stat[0] + max_component_stat[2] != mask1.shape[1] \
                    and max_component_stat[1] + max_component_stat[3] != mask1.shape[0]:

                if previous_image is None:
                    previous_image = mask1
                    is_writable = True

                if is_writable or compare_ssim(mask1, previous_image) < 0.8:
                    cv2.imwrite(os.path.join(dirname, str(i) + '.jpg'), crop_image)
                    previous_image = mask1
                    is_writable = False

        else:
            previous_image = None

    cap.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
