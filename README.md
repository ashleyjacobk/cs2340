# cs2340 team 10
Mahir Riki (mriki3)<br>
Nithish Sabapathy (nsabapathy6)<br>
Ashley Jacob (ajacob49)<br>
Yash Buddhdeo (ybuddhdeo3)<br>
Cassidy Dailly (cdailly3) <br>

# Travel Controller Simulation

This simulation system allows you to manage vehicles, locations, and routes in a transportation network. The system provides a command-line interface for creating and managing these entities.

## Getting Started

1. Compile the Java files:
```bash
javac *.java
```

2. Run the simulation:
```bash
java Main
```

## Available Commands

All commands use comma-separated values (CSV) format. The prompt will appear as `$>`.

### Vehicle Management

1. Create a vehicle:
```
create_vehicle,id,type,licensePlate,capacity
```
Example: `create_vehicle,V1,Bus,ABC123,50`

2. Display all vehicles:
```
display_vehicles
```

### Location Management

1. Create a location:
```
create_location,id,name
```
Example: `create_location,L1,Terminal`

2. Display all locations:
```
display_locations
```

### Route Management

1. Create a new route:
```
create_route,id
```
Example: `create_route,R1`

2. Add a location to a route:
```
add_location_to_route,routeId,locationId,position
```
Example: `add_location_to_route,R1,L1,0`

3. Remove a location from a route:
```
remove_location_from_route,routeId,position
```
Example: `remove_location_from_route,R1,0`

4. Display a route's locations:
```
display_route,routeId
```
Example: `display_route,R1`

### Vehicle Positioning and Tracking

1. Set a vehicle's position on a route:
```
set_vehicle_position,vehicleId,routeId,position
```
Example: `set_vehicle_position,V1,R1,0`

2. Display vehicles at a specific location:
```
display_vehicles_at_location,locationId
```
Example: `display_vehicles_at_location,L1`

3. Display vehicles assigned to a route:
```
display_vehicles_on_route,routeId
```
Example: `display_vehicles_on_route,R1`

### Other Commands

- Exit the simulation:
```
exit
```

## Example Session

Here's an example of a complete session:

```
$> create_vehicle,V1,Bus,ABC123,50
INFO: Vehicle created: V1

$> create_location,L1,Terminal
INFO: Location created: L1

$> create_location,L2,Station
INFO: Location created: L2

$> create_route,R1
INFO: Route created: R1

$> add_location_to_route,R1,L1,0
INFO: Location L1 added to route R1

$> add_location_to_route,R1,L2,1
INFO: Location L2 added to route R1

$> set_vehicle_position,V1,R1,0
INFO: Vehicle V1 positioned on route R1

$> display_vehicles_at_location,L1
V1 (Bus) - ABC123

$> display_route,R1
Route R1: [L1: Terminal, L2: Station]

$> exit
exit acknowledged
```

## Notes

- All IDs (vehicle, location, route) must be unique
- Positions in routes are 0-based
- Vehicles can only be positioned on routes they are assigned to
- Locations must exist before they can be added to routes
- Vehicles must exist before they can be positioned on routes
- Routes must exist before locations can be added to them

## Error Handling

The system will display error messages if:
- Invalid commands are entered
- Required parameters are missing
- Invalid IDs are referenced
- Invalid positions are specified
- Operations are attempted on non-existent entities
