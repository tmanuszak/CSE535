'''
This folder will add the category text file if given the absolute path
of the practice videos folder.
Ex: python3 add_category_files.py /home/ubuntu/HandGesturesPracticeVideos/
'''

import sys
from os import listdir
from os.path import isfile, isdir, join

practice_videos_folder = sys.argv[1]

gestures = [
	["AC Power", "Hardware"],
	["Algorithm", "Programming"],
	["Antenna", "Hardware"],
	["Authentication", "Cryptography"],
	["Authorization", "Cryptography"],
	["Bandwidth", "Networking"],
	["Bluetooth", "Networking"],
	["Browser", "Networking"],
	["Cloud Computing", "Data"],
	["Data Compression", "Data"],
	["Data Link Layer", "Data"],
	["Data Mining", "Data"],
	["Decryption", "Cryptography"],
	["Domain", "Networking"],
	["Email", "Networking"],
	["Exposure", "Cryptography"],
	["Filter", "Programming"],
	["Firewall", "Cryptography"],
	["Flooding", "Networking"],
	["Gateway", "Networking"],
	["Hacker", "Cryptography"],
	["Header", "Data"],
	["Hot Swap", "Hardware"],
	["Hyperlink", "Networking"],
	["Infrastructure", "Hardware"],
	["Integrity", "Cryptography"],
	["Internet", "Networking"],
	["Intranet", "Networking"],
	["Latency", "Data"],
	["Loopback", "Networking"],
	["Motherboard", "Hardware"],
	["Network", "Networking"],
	["Networking", "Networking"],
	["Network Layer", "Networking"],
	["Node", "Programming"],
	["Packet", "Data"],
	["Partition", "Hardware"],
	["Password Sniffing", "Cryptography"],
	["Patch", "Programming"],
	["Phishing", "Cryptography"],
	["Physical Layer", "Networking"],
	["Ping", "Networking"],
	["Port Scan", "Networking"],
	["Presentation Layer", "Networking"],
	["Protocol", "Networking"]]

for gesture in gestures:
	for directory in [d for d in listdir(practice_videos_folder) if isdir(join(practice_videos_folder, d))]:
		if gesture[0] in directory:
			with open(practice_videos_folder + directory + "/category.txt", "w") as f:
				f.write(gesture[1])

