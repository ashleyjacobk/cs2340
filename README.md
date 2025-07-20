# cs2340 team 10
# Running the Transit Simulation GUI (React)

## Quick Start

1. Install Node ≥ 18.x and npm (or yarn / pnpm).
2. In the project root, run:

```bash
cd frontend
npm install      # fetch React + Vite deps
npm run dev      # opens http://localhost:5173 in your browser
```

The browser UI lets you pick a **command category** (Vehicles, Routes, Locations, Passengers, Time, Hazards) and then a **specific command**. Each parameter appears as its own input/drop-down on the next row. Press **Run** to execute or type any raw CLI command in the optional box.

Key panels:

* **Output Log** – mirrors the CLI; errors are shown in red and trigger a dismissible alert.
* **Upcoming Events** – next events in the queue.
* **Network Map** – shows locations (blue), vehicles (red), routes (grey) with gridlines.
* **Clock** – current simulation time (minutes) in the header.

All commands listed below work in both the CLI and GUI.

#
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

---

## Features and Commands

All commands use a comma-separated format. Below are the available commands and their descriptions:

---

### Vehicle Management

1.  **Create a vehicle**
 Create a vehicle with a unique ID, type, capacity, route, current location, and speed.
 ```
 create_vehicle,<capacity>,<type>,<id>,<routeId>,<currentLocationId>,<speed>
 ```
 Example:
 ```
 create_vehicle,50,BUS,V1,R1,L1,40
 ```

2.  **Display all vehicles**
 Display all vehicles in service, ordered by their unique identifier.
 ```
 display_vehicles
 ```

---

### Location Management

1.  **Create a location**
 Create a location with a name, a unique ID, and coordinates (x, y).
 ```
 create_location,<name>,<id>,<x>,<y>
 ```
 Example:
 ```
 create_location,Downtown Crossing,L1,0,0
 ```

2.  **Display all locations**
 Display all valid locations, ordered by their unique identifier.
 ```
 display_locations
 ```

---

### Route Management

1.  **Create a route**
 Create an initially empty route with a unique ID and a specified vehicle type.
 ```
 create_route,<id>,<vehicleType>
 ```
 Example:
 ```
 create_route,R1,BUS
 ```

2.  **Add a location to a route**
 Add a given location to an arbitrary position in a route.
 ```
 add_location_to_route,<routeId>,<locationId>,<position>
 ```
 Example:
 ```
 add_location_to_route,R1,L1,0
 ```

3.  **Remove a location from a route**
 Remove a location from a given position in a route.
 ```
 remove_location_from_route,<routeId>,<position>
 ```
 Example:
 ```
 remove_location_from_route,R1,0
 ```

4.  **Display all locations in a route**
 Display all locations contained in a route in a clear and consistent sequence.
 ```
 display_locations_in_route,<routeId>
 ```
 Example:
 ```
 display_locations_in_route,R1
 ```

5.  **Display all routes**
 Display all route IDs.
 ```
 display_routes
 ```

---

### Vehicle Positioning and Tracking

1.  **Set a vehicle's position on a route**
 Designate a vehicle's current position on a given route.
 ```
 set_vehicle_position,<vehicleId>,<routeId>,<position>
 ```
 Example:
 ```
 set_vehicle_position,V1,R1,0
 ```

2.  **Display vehicles at a specific location**
 Display which vehicles are currently at a given location.
 ```
 display_vehicles_at_location,<locationId>
 ```
 Example:
 ```
 display_vehicles_at_location,L1
 ```

3.  **Display vehicles assigned to a route**
 Display which vehicles are assigned to a given route.
 ```
 display_vehicles_on_route,<routeId>
 ```
 Example:
 ```
 display_vehicles_on_route,R1
 ```

---

### Time and Event Management

1.  **Display current time**
 ```
 display_time
 ```

2.  **Advance time**
 ```
 advance_time,<time_to_advance>
 ```
 Example:
 ```
 advance_time,10
 ```

3. **Jump to a specific time**
 ```
 jump_to_time,<new_time>
 ```
 Example:
 ```
 jump_to_time,100
 ```

4.  **Set vehicle speed**
 ```
 set_vehicle_speed,<vehicleId>,<speed_kph>
 ```
 Example:
 ```
 set_vehicle_speed,V1,60
 ```

5.  **Advance time to next event**
 ```
 advance_time_to_next_event
 ```

6.  **Display next event time**
 ```
 display_next_event
 ```

7.  **Display vehicle status**
 ```
 display_vehicle_status,<vehicleId>
 ```
 Example:
 ```
 display_vehicle_status,V1
 ```

8.  **Display vehicle riders**
 Display the current number of riders on a given vehicle.
 ```
 display_vehicle_riders,<vehicleId>
 ```
 Example:
 ```
 display_vehicle_riders,V1
 ```

---

### Passenger Configuration

1.  **Set vehicle riders**  
Set the current number of riders already on a vehicle.
```
set_vehicle_riders,<vehicleId>,<numRiders>
```
Example:
```
set_vehicle_riders,V1,35
```

2.  **Set waiting passengers at a location**  
Adjust how many passengers are currently waiting at a stop.
```
set_waiting_passengers,<locationId>,<numWaiting>
```
Example:
```
set_waiting_passengers,L1,120
```

3.  **Set passenger exchange ranges**  
Configure the min / max numbers used when passengers debark, transfer, and board at a location.
```
set_passenger_ranges,<locationId>,<debarkLow>,<debarkHigh>,<transferLow>,<transferHigh>,<boardLow>,<boardHigh>
```
Example:
```
set_passenger_ranges,L1,0,10,0,5,0,15
```

---

### Hazard Management

1. **Create a hazard**  
Create a short-term or long-term hazard that affects travel times at one (or two) locations.
```
create_hazard,<description>,<id>,<type(short_term|long_term)>,<impact>,<location1Id>,[<location2Id>]
```
Example:
```
create_hazard,Track Maintenance,H1,short_term,5,L1
```

2. **Display all hazards**
```
display_hazards
```

3. **Display hazards at a location**
```
display_hazards_at_location,<locationId>
```

4. **Remove a hazard**
```
remove_hazard,<hazardId>
```

---

### Other Commands

-   **Exit the simulation**
 ```
 exit
 ```

-   **Help**
 ```
 help
 ```

---

## Notes

-   **IDs and names must be alphanumeric and not exceed 100 characters.**
-   **All referenced entities (routes, locations, vehicles) must exist before being used in commands.**
-   **Positions are zero-based.**
-   **Vehicle types are BUS, TRAM, U_BAHN, S_BAHN.**

---

## Examples

Below is an example session demonstrating typical usage:

```
$> create_location,Downtown Crossing,L1,0,0
INFO: Location created: Downtown Crossing at (0.0,0.0)

$> create_location,South Station,L2,10,10
INFO: Location created: South Station at (10.0,10.0)

$> create_route,R1,BUS
INFO: Route created: R1

$> add_location_to_route,R1,L1,0
INFO: Location Downtown Crossing added to route R1 at position 0

$> add_location_to_route,R1,L2,1
INFO: Location South Station added to route R1 at position 1

$> create_vehicle,50,BUS,V1,R1,L1,40
INFO: Vehicle created: V1
INFO: Departure event scheduled for vehicle V1 at time 0

$> display_vehicles
BUS V1 is at Downtown Crossing on route R1

$> display_locations
Downtown Crossing
South Station

$> display_routes
R1

$> display_locations_in_route,R1
[Downtown Crossing, South Station]

$> set_vehicle_position,V1,R1,1
INFO: Vehicle V1 positioned on route R1

$> display_vehicles_at_location,L2
BUS V1 is at South Station on route R1

$> display_vehicles_on_route,R1
BUS V1 is at South Station on route R1

$> display_time
0

$> advance_time,10
INFO: Advancing time from 0 to 10
INFO: Jumping time from 0 to 10
INFO: Advanced to time: 1 and executed ArrivalEvent{time=1, vehicle=V1, destination=South Station}
INFO: Time is now 10

$> help
Available commands:
create_vehicle, display_vehicles, create_location, display_locations, create_route, add_location_to_route, remove_location_from_route, display_locations_in_route, display_routes, set_vehicle_position, display_vehicles_at_location, display_vehicles_on_route, display_time, advance_time, jump_to_time, set_vehicle_speed, advance_time_to_next_event, display_next_event, display_vehicle_status, exit

$> exit
exit acknowledged
```