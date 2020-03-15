const express = require('express');

const router = express.Router();

const locationController = require('../controllers/locationController');

router.get('/locations', locationController.getLocations);
router.post('/location/add', locationController.saveLocation);

module.exports = router;