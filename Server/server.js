/**
 * @file server.js: handles concurrent requests
 */

// Video file folder that gets uploaded to
const videofolder = '/home/ubuntu/NewVideos';
const practicevideofolder = '/home/ubuntu/HandGesturesPracticeVideos';

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
				return res.status(500).send("An error occurred on the server while uploading.");
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
						cwd: '/home/ubuntu/posenet_nodejs_setup-master/',
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

				if (req.headers.algo.toLowerCase() === 'manuszak' || req.headers.algo.toLowerCase() === 'all') {
					execSync('python3 gesture_id_manuszak.py ' + practicevideofolder + '/ ' + videofolder + "/'" + FileName + "'",
						{
							timeout: 300000,
							cwd: '/home/ubuntu/posenet_nodejs_setup-master/Python\ Scripts/',
							stdio: 'ignore'
						});
					
					// Check if category file was made correctly
					if (!fs.existsSync(videofolder + "/" + FileName.replace('.mp4', '') + "/category_manuszak.txt")) {
						console.log("ERROR: " + FileName + " didn't classsify properly with manuszak algorithm.");
						return;
					} else {
						const category = fs.readFileSync(videofolder + '/' + FileName.replace('.mp4', '') + '/category_manuszak.txt'); 
						console.log(FileName + " succussfully categorized as " + category + " with manuszak algorithm.");
					}
				}

				if (req.headers.algo.toLowerCase() === 'wisdom' || req.headers.algo.toLowerCase() === 'all') {
					execSync('python3 gesture_id_wisdom.py ' + practicevideofolder + '/ ' + videofolder + "/'" + FileName + "'",
						{
							timeout: 300000,
							cwd: '/home/ubuntu/posenet_nodejs_setup-master/Python\ Scripts/',
							stdio: 'ignore'
						});
					
					// Check if category file was made correctly
					if (!fs.existsSync(videofolder + "/" + FileName.replace('.mp4', '') + "/category_wisdom.txt")) {
						console.log("ERROR: " + FileName + " didn't classsify properly with wisdom algorithm.");
						return;
					} else {
						const category = fs.readFileSync(videofolder + '/' + FileName.replace('.mp4', '') + '/category_wisdom.txt'); 
						console.log(FileName + " succussfully categorized as " + category + " with wisdom algorithm.");
					}
				}

				if (req.headers.algo.toLowerCase() === 'nair' || req.headers.algo.toLowerCase() === 'all') {
					execSync('python3 gesture_id_nair.py ' + practicevideofolder + '/ ' + videofolder + "/'" + FileName + "'",
						{
							timeout: 300000,
							cwd: '/home/ubuntu/posenet_nodejs_setup-master/Python\ Scripts/',
							stdio: 'ignore'
						});
					
					// Check if category file was made correctly
					if (!fs.existsSync(videofolder + "/" + FileName.replace('.mp4', '') + "/category_nair.txt")) {
						console.log("ERROR: " + FileName + " didn't classsify properly with nair algorithm.");
						return;
					} else {
						const category = fs.readFileSync(videofolder + '/' + FileName.replace('.mp4', '') + '/category_nair.txt'); 
						console.log(FileName + " succussfully categorized as " + category + " with nair algorithm.");
					}
				}

				if (req.headers.algo.toLowerCase() === 'lamba' || req.headers.algo.toLowerCase() === 'all') {
					execSync('python3 gesture_id_lamba.py ' + practicevideofolder + '/ ' + videofolder + "/'" + FileName + "'",
						{
							timeout: 300000,
							cwd: '/home/ubuntu/posenet_nodejs_setup-master/Python\ Scripts/',
							stdio: 'ignore'
						});
					
					// Check if category file was made correctly
					if (!fs.existsSync(videofolder + "/" + FileName.replace('.mp4', '') + "/category_lamba.txt")) {
						console.log("ERROR: " + FileName + " didn't classsify properly with lamba algorithm.");
						return;
					} else {
						const category = fs.readFileSync(videofolder + '/' + FileName.replace('.mp4', '') + '/category_lamba.txt'); 
						console.log(FileName + " succussfully categorized as " + category + " with lamba algorithm.");
					}
				}
				
				
				return;
			
			}
		})
	} else {
		return res.status(401).send("Unauthorized.");
	}
});

server.get('/classification', async (req, res) => {
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

