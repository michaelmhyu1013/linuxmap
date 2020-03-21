var net = require('net');
const fs = require('fs');

const dBFilePath = 'DB/locationData.json';

var rawdata = fs.readFileSync(dBFilePath);
var locationObj = JSON.parse(rawdata);
var locationList = locationObj.locations;

console.log(locationList);

var server = net.createServer(function(socket) {
	//var respData = "HTTP/1.1 200 OK\r\n\r\nHello"
	//socket.write('Echo server\r\n');
    //socket.pipe(socket);
       
	socket.setTimeout(10000, function() {
        socket.end('10 seconds passed, closing the socket\r\n');
        socket.destroy();
	});

	socket.on('data', function (data) {
		console.log('Received data from client: ' + data);

		try {
			let myObj = JSON.parse(data);
			myObj.userIP=socket.remoteAddress;
			console.log(myObj);

			// Write data into file
			locationList.push(myObj);
    
			let output={"locations":locationList};
			fs.writeFileSync(dBFilePath,JSON.stringify(output));
		} catch (error) {
			console.log("Something wrong, catch error here!");
//            console.log(error);
		}
	})

	// socket.on('connection', (data) => {
	// 	console.log('Client connected to this server ');
    // });
    
	socket.on('end', socket.end);

	socket.on('close', function() {
		console.log('disconnected');
	});
  
	socket.on('error', function(e) {
	  console.log('An unexpected error occured!');
	});

});

server.listen(9000);