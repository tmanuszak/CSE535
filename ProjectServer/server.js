/**
 * @file server.js: handles concurrent requests
 */

// Video file folder that gets uploaded to
const bpmvideofolder = '/home/ubuntu/BPMVideos';
const bpmresultsfolder = '/home/ubuntu/BPMResults';

// Server imports
const fs = require('fs');
const express = require('express');
const server = express();
const PORT = 3000;
const execSync = require('child_process').execSync;

server.use(express.static('public'));

// multer imports
const multer = require('multer');

var storage = multer.diskStorage({
	destination: function (req, file, callback) {
		callback(null, bpmvideofolder);
	},
	filename: function (req, file, callback) {
		callback(null, file.originalname);
	}
});

var upload = multer({ storage: storage }).single('videofile');

server.post('/upload', async (req, res) => {
	if (req.headers.user === 'CSE535Group') {
		upload(req, res, function (err) {
			if (err) {
				console.log(err);
				return res.status(500).send("An error occurred on the server while uploading.");
			} else {
				var FileName = req.file.filename;
				console.log("Uploaded file " + FileName + " to " + bpmvideofolder);
				res.status(200).send("Upload successful.");
				
				console.log("Starting to get the heart rate of " + FileName)				
				execSync('/home/ubuntu/miniconda3/envs/pyvhr/bin/python /home/ubuntu/BPMServer/get_bpm.py ' + bpmvideofolder + '/' + FileName + ' ' + FileName.replace('.mp4','') + ' ' + req.headers.time + ' ' + req.headers.coords, 
					{
						timeout: 300000, // 5 minutes
						cwd: '/home/ubuntu/BPMServer',
						stdio: 'ignore'
					});
				/*
				if (!fs.existsSync(bpmresultsfolder + '/' + FileName.replace('.mp4','_results.npy'))) {
					console.log('ERROR: ' + FileName + ' results did not get written.');
				} else if (!fs.existsSync(bpmresultsfolder + '/' + FileName.replace('.mp4','_time.npy'))) {
					console.log('ERROR: ' + FileName + ' time array did not get written.');
				} else if (!fs.existsSync(bpmresultsfolder + '/' + FileName.replace('.mp4','_coords.npy'))) {
					console.log('ERROR: ' FileName + + ' coords did not get written.');
				}*/
				if (!fs.existsSync(bpmresultsfolder + '/' + FileName.replace('.mp4','.png'))) {
					console.log("ERROR: " + FileName + " did not plot.");
					return;
				} else if (!fs.existsSync(bpmresultsfolder + '/' + FileName.replace('.mp4','_bpm.pickle'))) {
					console.log("ERROR: " + FileName + " did not pickle.");
					return;
				} else if (!fs.existsSync(bpmresultsfolder + '/' + FileName.replace('.mp4','_avg.pickle'))) {
					console.log("ERROR: " + FileName + " did not pickle avg.");
					return;
				} else if (!fs.existsSync(bpmresultsfolder + '/' + FileName.replace('.mp4','_time.pickle'))) {
					console.log("ERROR: " + FileName + " did not pickle time.");
					return;
				} else if (!fs.existsSync(bpmresultsfolder + '/' + FileName.replace('.mp4','_coords.pickle'))) {
					console.log("ERROR: " + FileName + " did not pickle coords.");
					return;
				} else if (!fs.existsSync(bpmresultsfolder + '/' + FileName.replace('.mp4','_start_time_millis.txt'))) {
					console.log("ERROR: " + FileName + " does not have a start time file.");
					return;
				}

				console.log("Successfully measured heart rate of " + FileName);

	
				return;
			
			}
		})
	} else {
		return res.status(401).send("Unauthorized.");
	}
});

server.get('/results', async (req, res) => {
	if (req.headers.user !== 'CSE535Group') {
		return res.status(401).send("Unauthorized to use this.");
	}

	if (!req.headers.file) {
		return res.status(400).send("No file was given");
	}

	file_requested = req.headers.file;

	try {
		if (!fs.existsSync(videofolder + "/" + file_requested)) {
			return res.status(400).send("The file requested does not exist.");
		}
		
		if (!fs.existsSync(videofolder + "/" + file_requested.replace('.mp4', '') + '/category.txt')) {
			return res.status(404).send("The requested file has not finished classification.");
		}

		const category = fs.readFileSync(videofolder + '/' + file_requested.replace('.mp4', '') + '/category.txt');
		console.log("Server returning category " + category + " for " + file_requested);
		res.status(200).send(category);
	} catch (err) {
		console.log("ERROR: an error occurred when trying to get the classification.");
		return res.status(500).send("Error on the server.");
	}
});

const hostname = '0.0.0.0';
server.listen(PORT, hostname, () => {
    console.log(`Server running at http://${hostname}:${PORT}/`);
  });

