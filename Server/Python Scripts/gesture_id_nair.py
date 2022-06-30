'''
This file will take in <incoming_vid> absolute path and <practice_vid> absolute path.
It will use Dynamic Time Warp to compare the normed wrist co-ordinates of the incoming video compared to 
all of the practice videos. It will keep track of the k-nearest practice videos to the new video.

Returns: The mode category from the top-k nearest practice videos as a category.txt file.

Ex:
python3 gesture_id.py /home/ubuntu/HandGesturesPracticeVideos/ /home/ubuntu/NewVideos/Email_PRACTICE_2_HariNair.mp4

Reference: https://dynamictimewarping.github.io/python/
'''


import csv
import sys
import numpy as np
from dtw import *
from os import listdir
from os.path import isfile, isdir, join, splitext
from statistics import mode

practice_vid = sys.argv[1]
incoming_vid = sys.argv[2]
k = 5 # k-value for top-k nearest videos (best results with 3 <= k <= 12 

right_wrist_X_norm, right_wrist_Y_norm = [], []
left_wrist_X_norm, left_wrist_Y_norm = [], []

with open(splitext(incoming_vid)[0] + "/key_points.csv") as csv_file:
  raw_data = csv.reader(csv_file, delimiter=',')
  next(raw_data, None) 
  for row in raw_data:
    right_wrist_X_norm.append((float(row[33]) - float(row[3])) / abs(float(row[18]) - float(row[21])))
    right_wrist_Y_norm.append((float(row[34]) - float(row[4])) / abs(float(row[4]) - (float(row[40])+float(row[37]))/2))
    left_wrist_X_norm.append((float(row[30]) - float(row[3])) / abs(float(row[18]) - float(row[21])))
    left_wrist_Y_norm.append((float(row[31]) - float(row[4])) / abs(float(row[4]) - (float(row[40])+float(row[37]))/2))

# Normed array for ncoming video
incoming_norm_arr = [np.asarray(right_wrist_X_norm), np.asarray(right_wrist_Y_norm), np.asarray(left_wrist_X_norm), np.asarray(left_wrist_Y_norm)]

# K-nearest Videos
topk = [] 

for directory in [d for d in listdir(practice_vid) if isdir(join(practice_vid, d))]:
	try:
		p_right_wrist_X_norm = np.load(practice_vid + directory + "/right_wrist_X_norm.npy")
		p_right_wrist_Y_norm = np.load(practice_vid + directory + "/right_wrist_Y_norm.npy")
		p_left_wrist_X_norm = np.load(practice_vid + directory + "/left_wrist_X_norm.npy")
		p_left_wrist_Y_norm = np.load(practice_vid + directory + "/left_wrist_Y_norm.npy")
		p_video_norm_arrs = [p_right_wrist_X_norm, p_right_wrist_Y_norm, p_left_wrist_X_norm, p_left_wrist_Y_norm]

		tup = (directory, sum([dtw(incoming_norm_arr[i], p_video_norm_arrs[i]).distance for i in range(len(p_video_norm_arrs))]))
		topk.append(tup)
		topk.sort(key = lambda x: x[1], reverse=True)
		if (len(topk) > k):
			topk = topk[1:]
	except Exception as e:
		print("An error occured while trying to DTW with " + directory)
		print(e)

categories = []
for tup in topk:
	with open(practice_vid + tup[0] + "/category.txt") as f:
		categories.append(f.readline().rstrip())
with open(splitext(incoming_vid)[0] + "/category_nair.txt", "w") as f:
	f.write(mode(categories))

print(incoming_vid + " is categorized as " + mode(categories))

