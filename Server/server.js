/**
 * @file server.js: handles concurrent requests
 */

// Server imports
const fs = require('fs');
const express = require('express');
const server = express();
const router = express.Router();
const PORT = 3000;


// multer imports
// uploaded images are saved in the folder "/upload_images"
const multer = require('multer');
const upload = multer({dest: '/home/ubuntu/test'});

// Upload image to S3 input bucket and send message to the request SQS queue
router.post('/', function (req, res) {
	
	console.log(req)


	res.end("yes")

});

const hostname = '0.0.0.0';
server.listen(PORT, hostname, () => {
    console.log(`Server running at http://${hostname}:${PORT}/`);
  });

