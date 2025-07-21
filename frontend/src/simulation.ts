/* eslint-disable max-classes-per-file */
// Core simulation logic (TypeScript) - simplified adaptation of the Java backend
// This file contains domain models, event engine, and command interpreter to
// enable the React GUI to run the transit simulation entirely in-browser.

export enum VehicleType {
  BUS = 'BUS',
  TRAM = 'TRAM',
  U_BAHN = 'U_BAHN',
  S_BAHN = 'S_BAHN',
}

export enum HazardType {
  SHORT_TERM = 'short_term',
  LONG_TERM = 'long_term',
}

// ---------------------------------------------------------------------------------
// Domain Models
// ---------------------------------------------------------------------------------

export class Location {
  constructor(
    public name: string,
    public id: string,
    public waitingPassengers = 0,
    // passenger range defaults
    private debarkLow = 0,
    private debarkHigh = 0,
    private transferLow = 0,
    private transferHigh = 0,
    private boardLow = 0,
    private boardHigh = 0,
    public x = 0,
    public y = 0,
  ) {}

  distanceTo(other: Location): number {
    const dx = this.x - other.x;
    const dy = this.y - other.y;
    return Math.sqrt(dx * dx + dy * dy);
  }

  // getters for passenger ranges
  getDebarkLow() {
    return this.debarkLow;
  }
  getDebarkHigh() {
    return this.debarkHigh;
  }
  getTransferLow() {
    return this.transferLow;
  }
  getTransferHigh() {
    return this.transferHigh;
  }
  getBoardLow() {
    return this.boardLow;
  }
  getBoardHigh() {
    return this.boardHigh;
  }

  setWaitingPassengers(num: number) {
    if (num < 0) {
      throw new Error('Waiting passenger count cannot be negative');
    }
    this.waitingPassengers = num;
  }

  setPassengerRanges(
    debarkLow: number,
    debarkHigh: number,
    transferLow: number,
    transferHigh: number,
    boardLow: number,
    boardHigh: number,
  ) {
    this.debarkLow = debarkLow;
    this.debarkHigh = debarkHigh;
    this.transferLow = transferLow;
    this.transferHigh = transferHigh;
    this.boardLow = boardLow;
    this.boardHigh = boardHigh;
  }
}

export class Route {
  private locations: Location[] = [];

  constructor(public id: string, public vehicleType: VehicleType) {}

  addLocation(loc: Location, position: number) {
    if (position < 0 || position > this.locations.length) {
      throw new Error('Invalid position');
    }
    this.locations.splice(position, 0, loc);
  }

  removeLocation(position: number) {
    if (position < 0 || position >= this.locations.length) {
      throw new Error('Invalid position');
    }
    this.locations.splice(position, 1);
  }

  getLocations() {
    return this.locations;
  }

  toString() {
    return this.id;
  }
}

export class Vehicle {
  private currentPassengers = 0;
  private previousLocation: Location | null = null;
  private inTransit = false;

  constructor(
    public capacity: number,
    public type: VehicleType,
    public id: string,
    public route: Route,
    public currentLocation: Location,
    public speed: number, // kph
  ) {
    if (capacity < 0) throw new Error('Capacity cannot be negative');
  }

  getCurrentPassengers() {
    return this.currentPassengers;
  }

  setCurrentPassengers(num: number) {
    if (num < 0 || num > this.capacity) throw new Error('Passengers out of range');
    this.currentPassengers = num;
  }

  isInTransit() {
    return this.inTransit;
  }

  getNextLocation(): Location | null {
    const locs = this.route.getLocations();
    if (locs.length === 0) return null;
    const idx = locs.indexOf(this.currentLocation);
    if (idx === -1) return locs[0];
    if (idx === locs.length - 1) return locs[0];
    return locs[idx + 1];
  }

  setInTransit(origin: Location, dest: Location) {
    this.previousLocation = origin;
    this.currentLocation = dest; // we store destination to compute next location but treat as in transit
    this.inTransit = true;
  }

  arriveAt(loc: Location) {
    this.previousLocation = this.currentLocation;
    this.currentLocation = loc;
    this.inTransit = false;
  }

  debarkPassengers(num: number) {
    const removed = Math.min(num, this.currentPassengers);
    this.currentPassengers -= removed;
    return removed;
  }

  boardPassengers(num: number) {
    const space = this.capacity - this.currentPassengers;
    const added = Math.min(num, space);
    this.currentPassengers += added;
    return added;
  }

  setSpeed(kph: number) {
    if (kph <= 0) throw new Error('Speed must be greater than zero');
    this.speed = kph;
  }

  getPreviousLocation() {
    return this.previousLocation;
  }

  toString() {
    const locStr = this.isInTransit()
      ? `in transit towards ${this.currentLocation?.name ?? 'unknown'}`
      : this.currentLocation?.name ?? 'unknown';
    return `${this.type} ${this.id} is at ${locStr} on route ${this.route}`;
  }
}

export class Hazard {
  constructor(
    private description: string,
    public id: string,
    public type: HazardType,
    public impact: number, // multiplier or minutes
    public location1: Location,
    public location2?: Location,
  ) {}

  isShortTerm() {
    return this.type === HazardType.SHORT_TERM;
  }
  isLongTerm() {
    return this.type === HazardType.LONG_TERM;
  }

  affectsLocation(loc: Location) {
    return (
      loc.id === this.location1.id || (this.location2 && loc.id === this.location2.id)
    );
  }

  affectsConnection(from: Location, to: Location) {
    return (
      (this.affectsLocation(from) && this.affectsLocation(to)) ||
      (this.affectsLocation(from) && !this.location2) ||
      (this.affectsLocation(to) && !this.location2)
    );
  }

  getDescription() {
    return this.description;
  }

  toString() {
    const loc2 = this.location2 ? ` & ${this.location2.name}` : '';
    return `${this.id}: ${this.description} (${this.type}) at ${this.location1.name}${loc2}`;
  }
}

// ---------------------------------------------------------------------------------
// Event Engine
// ---------------------------------------------------------------------------------
interface SimEvent {
  time: number;
  execute(): void;
  toString(): string;
}

class PriorityQueue<T extends SimEvent> {
  private queue: T[] = [];

  add(ev: T) {
    this.queue.push(ev);
    this.queue.sort((a, b) => a.time - b.time);
  }

  poll(): T | undefined {
    return this.queue.shift();
  }

  peek(): T | undefined {
    return this.queue[0];
  }

  isEmpty() {
    return this.queue.length === 0;
  }

  [Symbol.iterator]() {
    return this.queue[Symbol.iterator]();
  }

  toArray() {
    return [...this.queue];
  }
}

class TravelControllerInternal {
  // Internal state replicating Java version but kept private inside TravelController wrapper
  public vehicles = new Map<string, Vehicle>();
  public locations = new Map<string, Location>();
  public routes = new Map<string, Route>();
  public hazards: Hazard[] = [];
  public time = 0;
  public eventQueue = new PriorityQueue<SimEvent>();
}

class DepartureEvent implements SimEvent {
  constructor(
    public time: number,
    private vehicle: Vehicle,
    private ctrl: TravelController,
  ) {}

  execute() {
    const currentLocation = this.vehicle.currentLocation;
    const nextLocation = this.vehicle.getNextLocation();
    if (!currentLocation || !nextLocation) {
      this.ctrl.logError(`Vehicle ${this.vehicle.id} has no next location to depart to.`);
      return;
    }

    if (currentLocation.id === nextLocation.id) {
      this.ctrl.logInfo(`Vehicle ${this.vehicle.id} is already at ${currentLocation.name}.`);
      return;
    }

    this.vehicle.setInTransit(currentLocation, nextLocation);
    this.ctrl.logInfo(`Vehicle ${this.vehicle.id} departing from ${currentLocation.name} to ${nextLocation.name}.`);

    const dist = currentLocation.distanceTo(nextLocation);
    const minutes = this.ctrl.computeTravelMinutes(dist, this.vehicle.speed, currentLocation, nextLocation);
    const arrivalTime = this.time + minutes;
    this.ctrl.addEvent(new ArrivalEvent(arrivalTime, this.vehicle, currentLocation, nextLocation, this.ctrl));
  }

  toString() {
    const nextLoc = this.vehicle.getNextLocation()?.name ?? 'Unknown';
    return `Time ${this.time}: DEPART ${this.vehicle.id} -> ${nextLoc}`;
  }
}

class ArrivalEvent implements SimEvent {
  constructor(
    public time: number,
    private vehicle: Vehicle,
    private origin: Location,
    private destination: Location,
    private ctrl: TravelController,
  ) {}

  private randInRange(low: number, high: number) {
    if (low > high) return 0;
    if (low === high) return low;
    return Math.floor(Math.random() * (high - low + 1)) + low;
  }

  execute() {
    this.vehicle.arriveAt(this.destination);

    // passenger exchange simplified
    const loc = this.destination;
    const debark = this.randInRange(loc.getDebarkLow(), loc.getDebarkHigh());
    const debarked = this.vehicle.debarkPassengers(debark);

    const transfer = this.randInRange(loc.getTransferLow(), loc.getTransferHigh());

    const boardAttempt = this.randInRange(loc.getBoardLow(), loc.getBoardHigh());
    const boarded = this.vehicle.boardPassengers(Math.min(boardAttempt, loc.waitingPassengers));

    loc.setWaitingPassengers(loc.waitingPassengers - boarded + transfer);

    this.ctrl.logInfo(
      `Exchange at ${loc.name}: debarked=${debarked}, transfer=${transfer}, boarded=${boarded}, onboard=${this.vehicle.getCurrentPassengers()}, waiting=${loc.waitingPassengers}`,
    );

    let exchangeTime = 1;
    for (const h of this.ctrl.state.hazards) {
      if (h.isShortTerm() && h.affectsLocation(loc)) {
        this.ctrl.logInfo(`Applying short-term hazard ${h.getDescription()} to ${this.vehicle.id}.`);
        exchangeTime += h.impact;
      }
    }

    const departureTime = this.time + exchangeTime;
    this.ctrl.addEvent(new DepartureEvent(departureTime, this.vehicle, this.ctrl));
    this.ctrl.logInfo(`Vehicle ${this.vehicle.id} arrived at ${loc.name}. Departure at ${departureTime}.`);
  }

  toString() {
    return `Time ${this.time}: ARRIVAL ${this.vehicle.id} ${this.origin.name} -> ${this.destination.name}`;
  }
}

// ---------------------------------------------------------------------------------
// Public Controller exposed to React components
// ---------------------------------------------------------------------------------
export class TravelController {
  public state = new TravelControllerInternal();
  private messages: string[] = [];

  // ------------------------------- helpers -------------------------------
  private resetMessages() {
    this.messages = [];
  }

  private addMessage(line: string) {
    this.messages.push(line);
  }

  logInfo(text: string) {
    this.addMessage(`INFO: ${text}`);
  }
  logError(text: string) {
    this.addMessage(`ERROR: ${text}`);
  }

  // ------------------------------- entity helpers ------------------------
  private validId(id: string) {
    return /^[A-Za-z0-9]+$/.test(id) && id.length <= 100;
  }

  private isIdGloballyUnique(id: string) {
    return (
      !this.state.vehicles.has(id) &&
      !this.state.locations.has(id) &&
      !this.state.routes.has(id)
    );
  }

  // ------------------------------- core public API -----------------------
  executeCommand(line: string): string[] {
    this.resetMessages();

    const tokens = line.split(',').map((t) => t.trim());
    const cmd = tokens[0];
    try {
      switch (cmd) {
        case 'create_vehicle':
          this.cmdCreateVehicle(tokens);
          break;
        case 'display_vehicles':
          this.cmdDisplayVehicles();
          break;
        case 'create_location':
          this.cmdCreateLocation(tokens);
          break;
        case 'display_locations':
          this.cmdDisplayLocations();
          break;
        case 'create_route':
          this.cmdCreateRoute(tokens);
          break;
        case 'add_location_to_route':
          this.cmdAddLocationToRoute(tokens);
          break;
        case 'display_routes':
          this.cmdDisplayRoutes();
          break;
        case 'display_locations_in_route':
          this.cmdDisplayLocationsInRoute(tokens);
          break;
        case 'set_vehicle_position':
          this.cmdSetVehiclePosition(tokens);
          break;
        case 'display_vehicles_at_location':
          this.cmdDisplayVehiclesAtLocation(tokens);
          break;
        case 'display_vehicles_on_route':
          this.cmdDisplayVehiclesOnRoute(tokens);
          break;
        case 'remove_location_from_route':
          this.cmdRemoveLocationFromRoute(tokens);
          break;
        case 'set_vehicle_speed':
          this.cmdSetVehicleSpeed(tokens);
          break;
        case 'display_vehicle_status':
          this.cmdDisplayVehicleStatus(tokens);
          break;
        case 'display_vehicle_riders':
          this.cmdDisplayVehicleRiders(tokens);
          break;
        case 'set_vehicle_riders':
          this.cmdSetVehicleRiders(tokens);
          break;
        case 'set_waiting_passengers':
          this.cmdSetWaitingPassengers(tokens);
          break;
        case 'set_passenger_ranges':
          this.cmdSetPassengerRanges(tokens);
          break;
        case 'create_hazard':
          this.cmdCreateHazard(tokens);
          break;
        case 'display_hazards':
          this.cmdDisplayHazards();
          break;
        case 'display_hazards_at_location':
          this.cmdDisplayHazardsAtLocation(tokens);
          break;
        case 'remove_hazard':
          this.cmdRemoveHazard(tokens);
          break;
        case 'display_time':
          this.logInfo(`${this.state.time}`);
          break;
        case 'advance_time':
          this.cmdAdvanceTime(tokens);
          break;
        case 'jump_to_time':
          this.cmdJumpToTime(tokens);
          break;
        case 'advance_time_to_next_event':
          this.advanceToNextEvent();
          break;
        case 'display_next_event':
          this.displayNextEvent();
          break;
        case 'help':
          this.displayHelp();
          break;
        case 'exit':
          this.logInfo('exit acknowledged');
          break;
        default:
          this.logError(`Unknown command '${cmd}'. Type 'help' for assistance.`);
      }
    } catch (err) {
      if (err instanceof Error) {
        this.logError(err.message);
      } else {
        this.logError(String(err));
      }
    }

    return this.messages;
  }

  // ------------------------------- command implementations ---------------
  private cmdCreateVehicle(tokens: string[]) {
    if (tokens.length !== 7) {
      throw new Error('Usage: create_vehicle,<capacity>,<type>,<id>,<routeId>,<currentLocationId>,<speed>');
    }
    const capacity = Number(tokens[1]);
    const typeStr = tokens[2].toUpperCase().replace('-', '_');
    const id = tokens[3];
    const routeId = tokens[4];
    const locId = tokens[5];
    const speed = Number(tokens[6]);

    if (!this.validId(id) || !this.isIdGloballyUnique(id)) {
      throw new Error(`Invalid or non-unique vehicle id '${id}'.`);
    }

    const route = this.state.routes.get(routeId);
    if (!route) throw new Error(`Route '${routeId}' does not exist.`);
    if (route.vehicleType !== (VehicleType as any)[typeStr]) {
      throw new Error('Vehicle type does not match route type.');
    }

    const loc = this.state.locations.get(locId);
    if (!loc) throw new Error(`Location '${locId}' does not exist.`);

    if (!route.getLocations().includes(loc)) {
      throw new Error('Current location is not part of the route.');
    }

    const vehicle = new Vehicle(capacity, (VehicleType as any)[typeStr], id, route, loc, speed);
    this.state.vehicles.set(id, vehicle);
    this.logInfo(`Vehicle created: ${id}`);

    const departure = new DepartureEvent(this.state.time, vehicle, this);
    this.addEvent(departure);
    this.logInfo(`Departure scheduled for ${id} at time ${this.state.time}`);
  }

  private cmdDisplayVehicles() {
    if (this.state.vehicles.size === 0) {
      this.logInfo('No vehicles in service');
    } else {
      for (const v of this.state.vehicles.values()) {
        this.logInfo(v.toString());
      }
    }
  }

  private cmdCreateLocation(tokens: string[]) {
    if (tokens.length !== 5) {
      throw new Error('Usage: create_location,<name>,<id>,<x>,<y>');
    }
    const name = tokens[1];
    const id = tokens[2];
    const x = Number(tokens[3]);
    const y = Number(tokens[4]);

    if (!this.validId(id) || !this.isIdGloballyUnique(id)) {
      throw new Error(`Invalid or non-unique location id '${id}'.`);
    }

    const loc = new Location(name, id,
      0,          // waitingPassengers
      0, 0,       // debark range
      0, 0,       // transfer range
      0, 0,       // board range (low, high)
      x,
      y);
    this.state.locations.set(id, loc);
    this.logInfo(`Location created: ${name} at (${x},${y})`);
  }

  private cmdDisplayLocations() {
    if (this.state.locations.size === 0) this.logInfo('No locations available');
    else {
      for (const loc of this.state.locations.values()) {
        this.logInfo(loc.name);
      }
    }
  }

  private cmdCreateRoute(tokens: string[]) {
    if (tokens.length !== 3) {
      throw new Error('Usage: create_route,<id>,<vehicleType>');
    }
    const id = tokens[1];
    const typeStr = tokens[2].toUpperCase().replace('-', '_');

    if (!this.validId(id) || !this.isIdGloballyUnique(id)) {
      throw new Error(`Invalid or non-unique route id '${id}'.`);
    }

    const route = new Route(id, (VehicleType as any)[typeStr]);
    this.state.routes.set(id, route);
    this.logInfo(`Route created: ${id}`);
  }

  private cmdAddLocationToRoute(tokens: string[]) {
    if (tokens.length !== 4) {
      throw new Error('Usage: add_location_to_route,<routeId>,<locationId>,<position>');
    }
    const routeId = tokens[1];
    const locId = tokens[2];
    const pos = Number(tokens[3]);

    const route = this.state.routes.get(routeId);
    const loc = this.state.locations.get(locId);
    if (!route) throw new Error(`Route '${routeId}' not found.`);
    if (!loc) throw new Error(`Location '${locId}' not found.`);

    route.addLocation(loc, pos);
    this.logInfo(`Location ${loc.name} added to route ${routeId} at position ${pos}`);
  }

  private cmdRemoveLocationFromRoute(tokens: string[]) {
    if (tokens.length !== 3) {
      throw new Error('Usage: remove_location_from_route,<routeId>,<position>');
    }
    const route = this.state.routes.get(tokens[1]);
    if (!route) throw new Error('Route not found');
    const pos = Number(tokens[2]);
    route.removeLocation(pos);
    this.logInfo(`Removed location at position ${pos} from route ${route.id}`);
  }

  private cmdSetVehicleSpeed(tokens: string[]) {
    if (tokens.length !== 3) throw new Error('Usage: set_vehicle_speed,<vehicleId>,<speed_kph>');
    const veh = this.state.vehicles.get(tokens[1]);
    if (!veh) throw new Error('Vehicle not found');
    const speed = Number(tokens[2]);
    veh.setSpeed(speed);
    this.logInfo(`Vehicle ${veh.id} speed set to ${speed} kph`);
  }

  private cmdDisplayVehicleStatus(tokens: string[]) {
    if (tokens.length !== 2) throw new Error('Usage: display_vehicle_status,<vehicleId>');
    const veh = this.state.vehicles.get(tokens[1]);
    if (!veh) throw new Error('Vehicle not found');
    if (veh.isInTransit()) {
      const from = veh.getPreviousLocation()?.name ?? 'unknown';
      this.logInfo(`Vehicle ${veh.id} departed from ${from} and is in transit.`);
    } else {
      this.logInfo(`Vehicle ${veh.id} is at location ${veh.currentLocation.name}.`);
    }
  }

  private cmdDisplayVehicleRiders(tokens: string[]) {
    if (tokens.length !== 2) throw new Error('Usage: display_vehicle_riders,<vehicleId>');
    const veh = this.state.vehicles.get(tokens[1]);
    if (!veh) throw new Error('Vehicle not found');
    this.logInfo(`Vehicle ${veh.id} riders: ${veh.getCurrentPassengers()}`);
  }

  private cmdSetVehicleRiders(tokens: string[]) {
    if (tokens.length !== 3) throw new Error('Usage: set_vehicle_riders,<vehicleId>,<numRiders>');
    const veh = this.state.vehicles.get(tokens[1]);
    if (!veh) throw new Error('Vehicle not found');
    const num = Number(tokens[2]);
    veh.setCurrentPassengers(num);
    this.logInfo(`Vehicle ${veh.id} riders set to ${num}`);
  }

  private cmdSetWaitingPassengers(tokens: string[]) {
    if (tokens.length !== 3) throw new Error('Usage: set_waiting_passengers,<locationId>,<numWaiting>');
    const loc = this.state.locations.get(tokens[1]);
    if (!loc) throw new Error('Location not found');
    const num = Number(tokens[2]);
    loc.setWaitingPassengers(num);
    this.logInfo(`Waiting passengers at ${loc.id} set to ${num}`);
  }

  private cmdSetPassengerRanges(tokens: string[]) {
    if (tokens.length !== 8) {
      throw new Error('Usage: set_passenger_ranges,<locationId>,<debarkLow>,<debarkHigh>,<transferLow>,<transferHigh>,<boardLow>,<boardHigh>');
    }
    const loc = this.state.locations.get(tokens[1]);
    if (!loc) throw new Error('Location not found');
    const [debarkLow, debarkHigh, transferLow, transferHigh, boardLow, boardHigh] = tokens.slice(2).map(Number);
    loc.setPassengerRanges(debarkLow, debarkHigh, transferLow, transferHigh, boardLow, boardHigh);
    this.logInfo(`Passenger ranges updated for location ${loc.id}`);
  }

  private cmdCreateHazard(tokens: string[]) {
    if (tokens.length < 6 || tokens.length > 7) {
      throw new Error('Usage: create_hazard,<description>,<id>,<type(short_term|long_term)>,<impact>,<location1Id>,[<location2Id>]');
    }
    const description = tokens[1];
    const id = tokens[2];
    const typeStr = tokens[3];
    const impact = Number(tokens[4]);
    const loc1 = this.state.locations.get(tokens[5]);
    if (!loc1) throw new Error('Location1 not found');
    const loc2 = tokens.length === 7 ? this.state.locations.get(tokens[6]) : undefined;
    if (tokens.length === 7 && !loc2) throw new Error('Location2 not found');
    if (!this.isIdGloballyUnique(id)) throw new Error('Hazard id must be unique');
    const type = typeStr === 'short_term' ? HazardType.SHORT_TERM : HazardType.LONG_TERM;
    const hazard = new Hazard(description, id, type, impact, loc1, loc2);
    this.state.hazards.push(hazard);
    this.logInfo(`Hazard created: ${id}`);
  }

  private cmdDisplayHazards() {
    if (this.state.hazards.length === 0) this.logInfo('No hazards available');
    else this.state.hazards.forEach((h) => this.logInfo(h.toString()));
  }

  private cmdDisplayHazardsAtLocation(tokens: string[]) {
    if (tokens.length !== 2) throw new Error('Usage: display_hazards_at_location,<locationId>');
    const loc = this.state.locations.get(tokens[1]);
    if (!loc) throw new Error('Location not found');
    const list = this.state.hazards.filter((h) => h.affectsLocation(loc));
    if (list.length === 0) this.logInfo(`No hazards at location ${loc.id}`);
    else list.forEach((h) => this.logInfo(h.toString()));
  }

  private cmdRemoveHazard(tokens: string[]) {
    if (tokens.length !== 2) throw new Error('Usage: remove_hazard,<hazardId>');
    const idx = this.state.hazards.findIndex((h) => h.id === tokens[1]);
    if (idx === -1) throw new Error('Hazard not found');
    this.state.hazards.splice(idx, 1);
    this.logInfo(`Hazard ${tokens[1]} removed`);
  }

  private cmdDisplayRoutes() {
    if (this.state.routes.size === 0) this.logInfo('No routes available');
    else for (const r of this.state.routes.values()) this.logInfo(r.id);
  }

  private cmdDisplayLocationsInRoute(tokens: string[]) {
    if (tokens.length !== 2) {
      throw new Error('Usage: display_locations_in_route,<routeId>');
    }
    const route = this.state.routes.get(tokens[1]);
    if (!route) throw new Error('Route not found');
    this.logInfo(
      '[' +
        route
          .getLocations()
          .map((l) => l.name)
          .join(', ')+
        ']',
    );
  }

  private cmdSetVehiclePosition(tokens: string[]) {
    if (tokens.length !== 4) {
      throw new Error('Usage: set_vehicle_position,<vehicleId>,<routeId>,<position>');
    }
    const veh = this.state.vehicles.get(tokens[1]);
    const route = this.state.routes.get(tokens[2]);
    const pos = Number(tokens[3]);
    if (!veh) throw new Error('Vehicle not found');
    if (!route) throw new Error('Route not found');
    const locs = route.getLocations();
    if (pos < 0 || pos >= locs.length) throw new Error('Position out of range');
    veh.arriveAt(locs[pos]);
    veh.route = route;
    this.logInfo(`Vehicle ${veh.id} positioned on route ${route.id}`);
  }

  private cmdDisplayVehiclesAtLocation(tokens: string[]) {
    if (tokens.length !== 2) throw new Error('Usage: display_vehicles_at_location,<locationId>');
    const locId = tokens[1];
    const loc = this.state.locations.get(locId);
    if (!loc) throw new Error('Location not found');
    const vehicles = [...this.state.vehicles.values()].filter((v) => v.currentLocation.id === locId && !v.isInTransit());
    if (vehicles.length === 0) this.logInfo(`No vehicles at location ${locId}`);
    else this.logInfo(`Vehicles at location ${locId}: ${vehicles.map((v) => v.id).join(', ')}`);
  }

  private cmdDisplayVehiclesOnRoute(tokens: string[]) {
    if (tokens.length !== 2) throw new Error('Usage: display_vehicles_on_route,<routeId>');
    const routeId = tokens[1];
    const vehicles = [...this.state.vehicles.values()].filter((v) => v.route.id === routeId);
    if (vehicles.length === 0) this.logInfo(`No vehicles on route ${routeId}`);
    else this.logInfo(`Vehicles on route ${routeId}: ${vehicles.map((v) => v.id).join(', ')}`);
  }

  private cmdAdvanceTime(tokens: string[]) {
    if (tokens.length !== 2) throw new Error('Usage: advance_time,<minutes>');
    const minutes = Number(tokens[1]);
    if (minutes <= 0) throw new Error('minutes must be > 0');
    this.jumpToTime(this.state.time + minutes);
  }

  private cmdJumpToTime(tokens: string[]) {
    if (tokens.length !== 2) throw new Error('Usage: jump_to_time,<time>');
    const newTime = Number(tokens[1]);
    if (newTime <= this.state.time) throw new Error('Cannot go backwards in time');
    this.jumpToTime(newTime);
  }

  // ------------------------------- internal simulation controls ----------
  private addEvent(ev: SimEvent) {
    this.state.eventQueue.add(ev);
  }

  private advanceToNextEvent() {
    if (this.state.eventQueue.isEmpty()) {
      this.logError('No scheduled events');
      return;
    }
    const ev = this.state.eventQueue.poll()!;
    this.state.time = Math.max(this.state.time, ev.time);
    ev.execute();
    this.logInfo(`Time is now ${this.state.time}`);
  }

  private jumpToTime(newTime: number) {
    this.logInfo(`Jumping time from ${this.state.time} to ${newTime}`);
    while (!this.state.eventQueue.isEmpty() && this.state.eventQueue.peek()!.time <= newTime) {
      this.advanceToNextEvent();
    }
    this.state.time = newTime;
    this.logInfo(`Time is now ${this.state.time}`);
  }

  private displayNextEvent() {
    const next = this.state.eventQueue.peek();
    if (!next) this.logInfo('No scheduled events.');
    else this.logInfo(next.toString());
  }

  computeTravelMinutes(dist: number, speedKph: number, from: Location, to: Location) {
    const hours = dist / speedKph;
    let minutes = hours * 60;
    for (const h of this.state.hazards) {
      if (h.isLongTerm() && h.affectsConnection(from, to)) {
        this.logInfo(`Applying long-term hazard ${h.getDescription()} to travel time.`);
        minutes *= h.impact;
      }
    }
    return Math.max(1, Math.round(minutes));
  }

  displayHelp() {
    this.logInfo('See README for command list');
  }

  // ------------------------------- UI Helpers ----------------------------
  peekNextEvents(max = 10): string[] {
    return this.state.eventQueue
      .toArray()
      .slice(0, max)
      .map((ev) => ev.toString());
  }
} 