"""
This file will generate normed wrist coordinate arrays for ALL videos in 
<DIRECTORY>. (must be an absolute path and the keypoints csv must have been created.)
Ex:
python3 all_gestures_generate_normed_arrays.py /home/ubuntu/HandGesturesPracticeVideos/
"""


import csv
import numpy as np
import matplotlib.pyplot as plt
import dtw
import sys
from os import listdir
from os.path import isfile, isdir, join

videofolder = sys.argv[1]

# for each directory in the practice video folder make the normed
# wrist files from the directory/key_points.csv file
for directory in [d for d in listdir(videofolder) if isdir(join(videofolder, d))]:
	# Initializations
	left_wrist_X_raw = 0
	left_wrist_Y_raw = 0
	right_wrist_X_raw = 0
	right_wrist_Y_raw = 0

	# Normalizer values - shoulder, nose, hip
	nose_X, nose_Y = 0, 0
	left_shoulder_X, right_shoulder_X = 0, 0
	right_hip_Y, left_hip_Y = 0, 0

	right_wrist_X_norm, right_wrist_Y_norm = [], []
	left_wrist_X_norm, left_wrist_Y_norm = [], []

	with open(videofolder + directory + "/key_points.csv") as csv_file:
		raw_data = csv.reader(csv_file, delimiter=',')
		line = 0
		# Data extraction
		for row in raw_data:
			if not line:
				line = line+1
				continue
			line = line+1
			left_wrist_X_raw = float(row[30])
			left_wrist_Y_raw = float(row[31])
			right_wrist_X_raw = float(row[33])
			right_wrist_Y_raw = float(row[34])
			nose_X = float(row[3])
			nose_Y = float(row[4])
			left_shoulder_X = float(row[18])
			right_shoulder_X = float(row[21])
			right_hip_Y = float(row[40])
			left_hip_Y = float(row[37])

			right_wrist_X_norm.append((right_wrist_X_raw - nose_X) / abs(left_shoulder_X - right_shoulder_X))
			right_wrist_Y_norm.append((right_wrist_Y_raw - nose_Y) / abs(nose_Y - (right_hip_Y+left_hip_Y)/2))
			left_wrist_X_norm.append((left_wrist_X_raw - nose_X) / abs(left_shoulder_X - right_shoulder_X))
			left_wrist_Y_norm.append((left_wrist_X_raw - nose_Y) / abs(nose_Y - (right_hip_Y+left_hip_Y)/2))

	right_wrist_X_norm = np.asarray(right_wrist_X_norm)
	right_wrist_Y_norm = np.asarray(right_wrist_Y_norm)
	left_wrist_X_norm = np.asarray(left_wrist_X_norm)
	left_wrist_Y_norm = np.asarray(left_wrist_Y_norm)
	
	np.save(videofolder + directory + '/right_wrist_X_norm.npy', right_wrist_X_norm)
	np.save(videofolder + directory + '/right_wrist_Y_norm.npy', right_wrist_Y_norm)
	np.save(videofolder + directory + '/left_wrist_X_norm.npy', left_wrist_X_norm)
	np.save(videofolder + directory + '/left_wrist_Y_norm.npy', left_wrist_Y_norm)
	

# Running Dynamic Time Warping
# Reference: https://dynamictimewarping.github.io/python/
