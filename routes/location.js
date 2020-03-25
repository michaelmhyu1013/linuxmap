const express = require('express');

const router = express.Router();

const locationController = require('../controllers/locationController');

router.get('/locations', locationController.getLocations);
router.get('/locations/reset',locationController.resetLocations);
router.post('/location/add', locationController.saveLocation);

module.exports = router;