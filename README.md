# cs2340 team 10
Mahir Riki (mriki3)<br>
Nithish Sabapathy (nsabapathy6)<br>
Ashley Jacob (ajacob49)<br>
Yash Buddhdeo (ybuddhdeo3)<br>
Cassidy Dailly (cdailly3) <br>

# Travel Controller Simulation

This simulation system allows you to manage vehicles, locations, and routes in a transportation network. The system provides a command-line interface for creating and managing these entities.

---

## Getting Started

1. Compile and run the project using the `Makefile`:
   ```
   make
   ```

2. The system will start a command-line interface with a prompt (`$>`). You can enter commands to interact with the simulation.


## Features and Commands

All commands use a comma-separated format. Below are the available commands and their descriptions:

### Vehicle Management

1. **Create a vehicle**  
   Create a vehicle with a unique ID, type, license plate, capacity, direction, route, and current location.  
   ```
   create_vehicle,capacity,type,id,direction,routeId,currentLocationId
   ```
   Example:  
   ```
   create_vehicle,50,Bus,V1,North,R1,L1
   ```

2. **Display all vehicles**  
   Display all vehicles in service, ordered by their unique identifier.  
   ```
   display_vehicles
   ```

---

### Location Management

1. **Create a location**  
   Create a location where passengers can wait for vehicles.  
   ```
   create_location,id
   ```
   Example:  
   ```
   create_location,L1
   ```

2. **Display all locations**  
   Display all valid locations, ordered by their unique identifier.  
   ```
   display_locations
   ```

---

### Route Management

1. **Create a route**  
   Create an initially empty route to represent a sequence of locations.  
   ```
   create_route,id
   ```
   Example:  
   ```
   create_route,R1
   ```

2. **Add a location to a route**  
   Add a given location to an arbitrary position in a route.  
   ```
   add_location_to_route,routeId,locationId,position
   ```
   Example:  
   ```
   add_location_to_route,R1,L1,0
   ```

3. **Remove a location from a route**  
   Remove a location from a given position in a route.  
   ```
   remove_location_from_route,routeId,position
   ```
   Example:  
   ```
   remove_location_from_route,R1,0
   ```

4. **Display a route's locations**  
   Display all locations contained in a route in a clear and consistent sequence.  
   ```
   display_route,routeId
   ```
   Example:  
   ```
   display_route,R1
   ```

---

### Vehicle Positioning and Tracking

1. **Set a vehicle's position on a route**  
   Designate a vehicle's current position on a given route.  
   ```
   set_vehicle_position,vehicleId,routeId,position
   ```
   Example:  
   ```
   set_vehicle_position,V1,R1,0
   ```

2. **Display vehicles at a specific location**  
   Display which vehicles are currently at a given location.  
   ```
   display_vehicles_at_location,locationId
   ```
   Example:  
   ```
   display_vehicles_at_location,L1
   ```

3. **Display vehicles assigned to a route**  
   Display which vehicles are assigned to a given route.  
   ```
   display_vehicles_on_route,routeId
   ```
   Example:  
   ```
   display_vehicles_on_route,R1
   ```

---

### Other Commands

- **Exit the simulation**  
   Exit the program.  
   ```
   exit
   ```

- **Help**  
   Display a list of available commands.  
   ```
   help
   ```

---

## Example Session

Here’s an example of a complete session:

```
$> create_vehicle,50,Bus,V1,North,R1,L1
INFO: Vehicle created: V1

$> create_location,L1
INFO: Location created: L1

$> create_location,L2
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
V1 (Bus)

$> display_route,R1
Route R1: [L1, L2]

$> exit
exit acknowledged
```