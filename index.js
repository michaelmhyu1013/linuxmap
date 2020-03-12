let express = require('express')
let app = express();
let bodyParser = require('body-parser');
let path = require('path');
let connectDB = require('./DB/connection');

// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false })) // middleware

// parse application/json
app.use(bodyParser.json()) // middleware

let locationRoutes = require('./routes/location');

app.use(express.static(path.join(__dirname,'public')));

app.get('/', function (req,res) {
    res.send("This is location tracer app");
});

app.use(locationRoutes);

connectDB();

app.listen(process.env.PORT || 3000);



