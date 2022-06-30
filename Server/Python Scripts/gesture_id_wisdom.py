'''
This file will take in <new_video_mp4> absolute path and <practice_videos_folder> absolute path.
It will use Dynamic Time Warp to compare the normed wrist coords of the new video comared to 
all of the practice videos. It will keep track of the k closest practice videos to the new video.

Returns: The mode category from the top k closest practice videos as a category.txt file.

Ex:
python3 gesture_id.py /home/ubuntu/HandGesturesPracticeVideos/ /home/ubuntu/NewVideos/Email_PRACTICE_2_Manuszak.mp4

'''


# Running Dynamic Time Warping
# Reference: https://dynamictimewarping.github.io/python/

import csv
import numpy as np
import matplotlib.pyplot as plt
from dtw import *
import sys
from os import listdir
from os.path import isfile, isdir, join, splitext
from statistics import mode

practice_videos_folder = sys.argv[1]
new_video_mp4 = sys.argv[2]
k = 10

with open(splitext(new_video_mp4)[0] + "/key_points.csv") as csv_file:
    raw_data = csv.reader(csv_file, delimiter=',')
    next(raw_data)
    right_wrist_X_norm, right_wrist_Y_norm, left_wrist_X_norm, left_wrist_Y_norm = [], [], [], []
    for row in raw_data:
        nose_X = float(row[3])
        nose_Y = float(row[4])
        left_wrist_X_raw = float(row[30])
        left_wrist_Y_raw = float(row[31])
        right_wrist_X_raw = float(row[33])
        right_wrist_Y_raw = float(row[34])
        left_shoulder_X = float(row[18])
        right_shoulder_X = float(row[21])
        left_shoulder_Y = float(row[19])
        right_shoulder_Y = float(row[22])

        right_wrist_X_norm.append((right_wrist_X_raw - nose_X) / abs(left_shoulder_X - right_shoulder_X))
        right_wrist_Y_norm.append((right_wrist_Y_raw - nose_Y) / abs(nose_Y - (right_shoulder_Y+left_shoulder_Y)/2))
        left_wrist_X_norm.append((left_wrist_X_raw - nose_X) / abs(left_shoulder_X - right_shoulder_X))
        left_wrist_Y_norm.append((left_wrist_X_raw - nose_Y) / abs(nose_Y - (right_shoulder_Y+left_shoulder_Y)/2))

right_wrist_X_norm = np.asarray(right_wrist_X_norm)
right_wrist_Y_norm = np.asarray(right_wrist_Y_norm)
left_wrist_X_norm = np.asarray(left_wrist_X_norm)
left_wrist_Y_norm = np.asarray(left_wrist_Y_norm)
new_video_norm_arrs = [right_wrist_X_norm, right_wrist_Y_norm, left_wrist_X_norm, left_wrist_Y_norm]


topk = []
for directory in [d for d in listdir(practice_videos_folder) if isdir(join(practice_videos_folder, d))]:
    try:
        with open(practice_videos_folder + directory + "/key_points.csv") as csv_file:
            raw_data = csv.reader(csv_file, delimiter=',')
            next(raw_data)
            right_wrist_X_norm, right_wrist_Y_norm, left_wrist_X_norm, left_wrist_Y_norm = [], [], [], []
            for row in raw_data:
                nose_X = float(row[3])
                nose_Y = float(row[4])
                left_wrist_X_raw = float(row[30])
                left_wrist_Y_raw = float(row[31])
                right_wrist_X_raw = float(row[33])
                right_wrist_Y_raw = float(row[34])
                left_shoulder_X = float(row[18])
                right_shoulder_X = float(row[21])
                left_shoulder_Y = float(row[19])
                right_shoulder_Y = float(row[22])

                right_wrist_X_norm.append((right_wrist_X_raw - nose_X) / abs(left_shoulder_X - right_shoulder_X))
                right_wrist_Y_norm.append((right_wrist_Y_raw - nose_Y) / abs(
                nose_Y - (right_shoulder_Y + left_shoulder_Y) / 2))
                left_wrist_X_norm.append((left_wrist_X_raw - nose_X) / abs(left_shoulder_X - right_shoulder_X))
                left_wrist_Y_norm.append((left_wrist_X_raw - nose_Y) / abs(
                nose_Y - (right_shoulder_Y + left_shoulder_Y) / 2))

        right_wrist_X_norm = np.asarray(right_wrist_X_norm)
        right_wrist_Y_norm = np.asarray(right_wrist_Y_norm)
        left_wrist_X_norm = np.asarray(left_wrist_X_norm)
        left_wrist_Y_norm = np.asarray(left_wrist_Y_norm)
        practice_video_norm_arrs = [right_wrist_X_norm, right_wrist_Y_norm, left_wrist_X_norm, left_wrist_Y_norm]

        tup = (directory, sum([dtw(new_video_norm_arrs[i], practice_video_norm_arrs[i]).distance for i in range(len(new_video_norm_arrs))]))
        topk.append(tup)
    except Exception as e:
        print("An error occured while trying to DTW with " + directory)
        print(e)
topk.sort(key = lambda x: x[1], reverse=False)
topk = topk[:k]
categories = []
for tup in topk:
    with open(practice_videos_folder + tup[0] + "/category.txt") as f:
        categories.append(f.readline().rstrip())
with open(splitext(new_video_mp4)[0] + "/category_wisdom.txt", "w") as f:
    f.write(mode(categories))

print(new_video_mp4 + " is categorized as " + mode(categories))

