const mongoose = require('mongoose');

const URI = "mongodb+srv://dbUser:dbUser@cluster0-acgg8.mongodb.net/test?retryWrites=true&w=majority";

const connectDB = async() => {
    await mongoose.connect(URI, { 
        useUnifiedTopology: true, 
        useNewUrlParser: true,
        useFindAndModify: false
    });
};

module.exports = connectDB;