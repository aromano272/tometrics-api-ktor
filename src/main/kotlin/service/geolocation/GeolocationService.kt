package com.tometrics.api.service.geolocation

interface GeolocationService : ReverseGeocodingService, GeolocationAutocompleteService

class DefaultGeolocationService(
    private val reverseGeocodingService: ReverseGeocodingService,
    private val geolocationAutocompleteService: GeolocationAutocompleteService,
) : ReverseGeocodingService by reverseGeocodingService,
    GeolocationAutocompleteService by geolocationAutocompleteService,
    GeolocationService