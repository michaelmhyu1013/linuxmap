let mongoose = require('mongoose')

let userSchema = new mongoose.Schema({
    latitude: String,
    longitude: String,
    timeStamp: String
})

module.exports = mongoose.model('Location', userSchema)