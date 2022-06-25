/**
 * @file server.js: handles concurrent requests
 */

// Video file folder
const videofolder = '/home/ubuntu/test';

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
		callback(null, videofolder);
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
				res.status(500).send("An error occurred on the server while uploading.");
			} else {
				var FileName = req.file.filename;
				console.log("Uploaded file " + FileName + " to " + videofolder);
				res.status(200).send("Upload successful.");
				
				// If the upload was a rewrite, we need to remove all frames and key points
				if (fs.existsSync(videofolder + "/" + FileName.replace('.mp4', '') + "/")) {
					fs.rmdirSync(videofolder + "/" + FileName.replace('.mp4', '') + "/", { recursive: true }, (err) => {
						if (err) {
							throw err;
						}
					});
					console.log("Removed existing " + FileName + " directory with frames and keypoints.");
				}
				
				// Make frames
				// This process will stop if not completed in 2 minutes
				execSync('python3 Frames_Extractor.py', 
					{ 
						timeout: 120000, 
						cwd: '/home/ubuntu/posenet_nodejs_setup-master/Python\ Scripts/' 
					});
				
				// If 0.png doesnt exist, then there was an error durring framing
				if (!fs.existsSync(videofolder + "/" + FileName.replace('.mp4', '') + "/0.png")) {
					console.log("ERROR: " + FileName + " didn't get framed properly.");
					return;
				} else {
					console.log(FileName + " successfully framed.");
				}

				// Get the JSON keypoints from the file within 5 minutes
				execSync('node scale_to_videos.js ' + videofolder + "/",
					{
						timeout: 300000,
						cwd: 'home/ubuntu/posenet_nodejs_setup-master/',
						stdio: 'ignore'
					});
				
				// Check that the json keypoints was made
				if (!fs.existsSync(videofolder + "/" + FileName.replace('.mp4', '') + "/key_points.json")) {
					console.log("ERROR: " + FileName + " didn't get JSON keypoints properly.");
					return;
				} else {
					console.log(FileName + " succussfully made JSON keypoints.");
				}

				// Convert JSON keypoints to CSV within 1 minute
				execSync('python3 convert_to_csv.py ' + videofolder + '/',
					{
						timeout: 60000,
						cwd: '/home/ubuntu/posenet_nodejs_setup-master/Python\ Scripts/'
					});		

				// Check if csv file was made correctly
				if (!fs.existsSync(videofolder + "/" + FileName.replace('.mp4', '') + "/key_points.csv")) {
					console.log("ERROR: " + FileName + " didn't get CSV keypoints properly.");
					return;
				} else {
					console.log(FileName + " succussfully made CSV keypoints.");
				}

				return;
			
			}
		})
	} else {
		res.status(401).send("Unauthorized.");
	}
});

const hostname = '0.0.0.0';
server.listen(PORT, hostname, () => {
    console.log(`Server running at http://${hostname}:${PORT}/`);
  });

