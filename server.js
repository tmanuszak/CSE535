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
				console.log("Uploaded file " + FileName);
				res.status(200).send("Upload successful.");
			}
		})
	} else {
		res.send(401).send("Unauthorized.");
	}
});

const hostname = '0.0.0.0';
server.listen(PORT, hostname, () => {
    console.log(`Server running at http://${hostname}:${PORT}/`);
  });

