/**
 * @file server.js: handles concurrent requests
 */

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
        callback(null, '/home/ubuntu/test');
    },
    filename: function (req, file, callback) {
        callback(null, file.originalname);
    }
});

var upload = multer({ storage: storage }).single('videofile');

server.post('/', async (req, res) => {
		console.log(req);
    upload(req, res, function (err) {
        if (err) {
            console.log(err)
        } else {
        		var FileName = req.file.filename;
            console.log("Uploaded file " + FileName);
						res.status(200).send(FileName);
        }
    })
});

const hostname = '0.0.0.0';
server.listen(PORT, hostname, () => {
    console.log(`Server running at http://${hostname}:${PORT}/`);
  });

