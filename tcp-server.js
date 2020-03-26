var net = require('net');
const fs = require('fs');

const dBFilePath = 'DB/locationData.json';

/*------------------------------------------------------------------------------------------------------------------
-- FUNCTION: net.createServer
--
-- DATE: March 23, 2020
--
-- REVISIONS: N/A
--
-- DESIGNER: Peter Xiong
--
-- PROGRAMMER: Peter Xiong
--
-- INTERFACE:       net.createServer(function(socket))
--
-- RETURNS: void.
-- 
-- NOTES:
-- Create TCP server, which responses to TCP client request to set up a communication channel.
----------------------------------------------------------------------------------------------------------------------*/
var server = net.createServer(function(socket) {

	socket.setTimeout(300000, function() {
        socket.end('5 minutes passed, closing the socket\r\n');
        socket.destroy();
	});

	socket.on('data', function (data) {
		console.log('Received data from client: ' + data);

		try {
			// Get previous data from file before adding new data into it.
			var rawdata = fs.readFileSync(dBFilePath);
			var locationObj = JSON.parse(rawdata);
			var locationList = locationObj.locations;

			let myObj = JSON.parse(data);
			myObj.userIP=socket.remoteAddress;
			console.log(myObj);

			// Write data into file
			locationList.push(myObj);
    
			let output={"locations":locationList};
			fs.writeFileSync(dBFilePath,JSON.stringify(output));
		} catch (error) {
            console.log(error);
		}
	})
   
	socket.on('end', socket.end);

	socket.on('close', function() {
		console.log('disconnected');
	});
  
	socket.on('error', function(e) {
	  console.log('An unexpected error occured!');
	});

});

server.listen(9000);