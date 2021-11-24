# API Documentation for AgilityAds
- Get a list of all vehicles
```
experentialmarketing.ai/api/vehicles
```
- Get a vehicle by id
```
experentialmarketing.ai/api/vehicle/b00
```
- Get all location data for a vehicle
```
experentialmarketing.ai/api/vehicle/b00/locations
```
- Get locations after a time/date using epoch
```
//for this example were using valentines day and the epoch
experentialmarketing.ai/api/vehicle/b00/locations?from=1613264461
```
- Get locations before a time/date using epoch
```
//for this example were using memorial day
experentialmarketing.ai/api/vehicle/b00/locations?to=1622422861
```
- Get locations before a time/date and after a time/date using epoch
```
//for this example we want all locations after valentines day and before memorial day
experentialmarketing.ai/api/vehicle/b00/locations?from=1613264461&to=1622422861
```
- Get locations for a vehicle by id with a result set limit
```
//for this example we want all locations after valentines day and before memorial day
experentialmarketing.ai/api/vehicle/b00/locations?limit=1000
```
