'''
arg[1] = /home/ubuntu/<FileName>.mp4
arg[2] = <FileName> (no extension)
arg[3] = time (millis)
arg[4] = coords
'''

from pyVHR.analysis.pipeline import Pipeline
import matplotlib.pyplot as plt
import sys
import time as t
import numpy as np
from os.path import exists
import sys
import pickle
from brokenaxes import brokenaxes

# args
video_file_path = sys.argv[1]
filename = sys.argv[2]
video_time_millis = int(sys.argv[3])
coords = sys.argv[4]

start = t.time()

pipe = Pipeline()
time, BPM, uncertainty = pipe.run_on_video(video_file_path, roi_approach="hol", roi_method="convexhull")

print(t.time() - start)

BPM_parsed = []
time_parsed = []
stddev = np.std(BPM)
m = np.mean(BPM)

# make outliers mean
for i in range(len(BPM)):
	if (np.abs(BPM[i] - m) > 2.5 * stddev):
		BPM[i] = m

# moving average
average_bpm = []
for i in range(len(BPM) - 5 + 1):
	average_bpm.append(np.mean(BPM[i:i+5]))

for i in range(5 - 1):
	average_bpm.insert(0, np.nan)

# move the time array values if necessary
# if no start time file exists, this is the first vid, write this time to start time file
if (exists('/home/ubuntu/BPMResults/' + filename + '_start_time_millis.txt') is False):
	with open('/home/ubuntu/BPMResults/' + filename + '_start_time_millis.txt', 'w') as file:
		file.write(str(video_time_millis))
	for i in range(len(time)):
		time[i] = time[i] / 60 # so its in mintutes
	ax = plt.figure()
	plt.plot(time, BPM, 'k.-', label='Heart Rate Data')
	#plt.text(time[0], BPM[0]+30, coords, verticalalignment='bottom')		
	plt.plot(time, average_bpm, 'r.-', label='5 second moving avg.')
	plt.legend()
	plt.xlabel("Time (minutes)")
	plt.ylabel("BPM")
	plt.savefig('/home/ubuntu/BPMResults/' + filename + '.png')
	pickle.dump(np.expand_dims(BPM, axis=0).tolist(), open('/home/ubuntu/BPMResults/' + filename + '_bpm.pickle','wb'))
	pickle.dump(np.expand_dims(average_bpm, axis=0).tolist(), open('/home/ubuntu/BPMResults/' + filename + '_avg.pickle','wb'))
	pickle.dump(np.expand_dims(time, axis=0).tolist(), open('/home/ubuntu/BPMResults/' + filename + '_time.pickle','wb'))
	pickle.dump([[coords]], open('/home/ubuntu/BPMResults/' + filename + '_coords.pickle','wb'))
else:
	with open('/home/ubuntu/BPMResults/' + filename + '_start_time_millis.txt', 'r') as file:
		start_time = int(file.read())
		for i in range(len(time)):
			time[i] = ((video_time_millis - start_time) / 60000) + (time[i] / 60)  # minutes past first upload
	
	old_bpm = pickle.load(open('/home/ubuntu/BPMResults/' + filename + '_bpm.pickle','rb'))
	old_average_bpm = pickle.load(open('/home/ubuntu/BPMResults/' + filename + '_avg.pickle','rb'))
	old_time = pickle.load(open('/home/ubuntu/BPMResults/' + filename + '_time.pickle','rb'))
	old_coords = pickle.load(open('/home/ubuntu/BPMResults/' + filename + '_coords.pickle','rb'))
	
	print(old_bpm)

	old_bpm.append(BPM.tolist())
	old_average_bpm.append(average_bpm)
	old_time.append(time.tolist())
	old_coords.append([coords])
	
	BPM = old_bpm
	average_bpm = old_average_bpm
	time = old_time
	coords = old_coords

	print(BPM)
	pickle.dump(BPM, open('/home/ubuntu/BPMResults/' + filename + '_bpm.pickle','wb'))
	pickle.dump(average_bpm, open('/home/ubuntu/BPMResults/' + filename + '_avg.pickle','wb'))
	pickle.dump(time, open('/home/ubuntu/BPMResults/' + filename + '_time.pickle','wb'))
	pickle.dump(coords, open('/home/ubuntu/BPMResults/' + filename + '_coords.pickle','wb'))
	
	ax = plt.figure()
	# get x axis breaks
	xax = tuple((i[0],i[-1]) for i in time)
	bax = brokenaxes(xlims=xax, hspace=.05)	

	for i in range(len(BPM)):
		if i == 0:
			bax.plot(time[i], BPM[i], 'k.-', label="Heart Rate Data")
			bax.plot(time[i], average_bpm[i], 'r.-', label="5 second moving avg.")
			#bax.text(time[i][5], min(BPM[i])-10, coords[i], verticalalignment='bottom')	
		else:
			bax.plot(time[i], BPM[i], 'k.-')
			bax.plot(time[i], average_bpm[i], 'r.-')
			#bax.text(time[i][5], min(BPM[i])-10, coords[i], verticalalignment='bottom')	
		
	bax.legend()
	bax.set_xlabel("Time (minutes)")
	bax.set_ylabel("BPM")
	plt.savefig('/home/ubuntu/BPMResults/' + filename + '.png')
	

