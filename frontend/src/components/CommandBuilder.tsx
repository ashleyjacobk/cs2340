import { useState } from 'react';
import {
  Box,
  Stack,
  TextField,
  MenuItem,
  FormControl,
  InputLabel,
  Select,
  Button,
} from '@mui/material';
import { TravelController, VehicleType } from '../simulation';

// Type definitions for command metadata
interface ParamDef {
  name: string; // key used when building command string
  label: string; // human-readable
  type: 'text' | 'number' | 'select';
  options?: (ctrl: TravelController) => string[]; // when type === 'select'
  placeholder?: string;
}

interface CommandDef {
  key: string; // command keyword
  label: string; // displayed in dropdown
  params: ParamDef[]; // ordered list of parameter definitions
  category: string; // category key
}

interface Props {
  controller: TravelController;
  onSubmit: (command: string) => void;
}

function CommandBuilder({ controller, onSubmit }: Props) {
  // ------------------------------ definitions -----------------------------
  const cmdDefs: CommandDef[] = [
    {
      key: 'create_vehicle',
      label: 'Create Vehicle',
      params: [
        { name: 'capacity', label: 'Capacity', type: 'number' },
        {
          name: 'type',
          label: 'Vehicle Type',
          type: 'select',
          options: () => Object.values(VehicleType),
        },
        { name: 'id', label: 'Vehicle ID', type: 'text' },
        {
          name: 'routeId',
          label: 'Route',
          type: 'select',
          options: (c) => [...c.state.routes.keys()],
        },
        {
          name: 'currentLocationId',
          label: 'Current Location',
          type: 'select',
          options: (c) => [...c.state.locations.keys()],
        },
        { name: 'speed', label: 'Speed (kph)', type: 'number' },
      ],
      category: 'vehicles',
    },
    {
      key: 'create_location',
      label: 'Create Location',
      params: [
        { name: 'name', label: 'Name', type: 'text' },
        { name: 'id', label: 'Location ID', type: 'text' },
        { name: 'x', label: 'X Coordinate', type: 'number' },
        { name: 'y', label: 'Y Coordinate', type: 'number' },
      ],
      category: 'locations',
    },
    {
      key: 'create_route',
      label: 'Create Route',
      params: [
        { name: 'id', label: 'Route ID', type: 'text' },
        {
          name: 'vehicleType',
          label: 'Vehicle Type',
          type: 'select',
          options: () => Object.values(VehicleType),
        },
      ],
      category: 'routes',
    },
    {
      key: 'add_location_to_route',
      label: 'Add Location to Route',
      params: [
        {
          name: 'routeId',
          label: 'Route',
          type: 'select',
          options: (c) => [...c.state.routes.keys()],
        },
        {
          name: 'locationId',
          label: 'Location',
          type: 'select',
          options: (c) => [...c.state.locations.keys()],
        },
        { name: 'position', label: 'Position', type: 'number' },
      ],
      category: 'routes',
    },
    {
      key: 'remove_location_from_route',
      label: 'Remove Location from Route',
      params: [
        {
          name: 'routeId',
          label: 'Route',
          type: 'select',
          options: (c) => [...c.state.routes.keys()],
        },
        { name: 'position', label: 'Position', type: 'number' },
      ],
      category: 'routes',
    },
    {
      key: 'display_vehicles',
      label: 'Display All Vehicles',
      params: [],
      category: 'vehicles',
    },
    {
      key: 'display_locations',
      label: 'Display All Locations',
      params: [],
      category: 'locations',
    },
    {
      key: 'display_routes',
      label: 'Display All Routes',
      params: [],
      category: 'routes',
    },
    {
      key: 'display_locations_in_route',
      label: 'Display Locations in Route',
      params: [
        {
          name: 'routeId',
          label: 'Route',
          type: 'select',
          options: (c) => [...c.state.routes.keys()],
        },
      ],
      category: 'routes',
    },
    {
      key: 'set_vehicle_position',
      label: 'Set Vehicle Position',
      params: [
        {
          name: 'vehicleId',
          label: 'Vehicle',
          type: 'select',
          options: (c) => [...c.state.vehicles.keys()],
        },
        {
          name: 'routeId',
          label: 'Route',
          type: 'select',
          options: (c) => [...c.state.routes.keys()],
        },
        { name: 'position', label: 'Position', type: 'number' },
      ],
      category: 'vehicles',
    },
    {
      key: 'advance_time',
      label: 'Advance Time',
      params: [{ name: 'minutes', label: 'Minutes', type: 'number' }],
      category: 'time',
    },
    {
      key: 'jump_to_time',
      label: 'Jump to Time',
      params: [{ name: 'newTime', label: 'New Time', type: 'number' }],
      category: 'time',
    },
    {
      key: 'advance_time_to_next_event',
      label: 'Advance Time to Next Event',
      params: [],
      category: 'time',
    },
    {
      key: 'display_next_event',
      label: 'Display Next Event',
      params: [],
      category: 'time',
    },
    {
      key: 'display_time',
      label: 'Display Current Time',
      params: [],
      category: 'time',
    },
    {
      key: 'set_vehicle_speed',
      label: 'Set Vehicle Speed',
      category: 'vehicles',
      params: [
        {
          name: 'vehicleId',
          label: 'Vehicle',
          type: 'select',
          options: (c) => [...c.state.vehicles.keys()],
        },
        { name: 'speed', label: 'Speed (kph)', type: 'number' },
      ],
    },
    {
      key: 'display_vehicle_status',
      label: 'Display Vehicle Status',
      category: 'vehicles',
      params: [
        {
          name: 'vehicleId',
          label: 'Vehicle',
          type: 'select',
          options: (c) => [...c.state.vehicles.keys()],
        },
      ],
    },
    {
      key: 'display_vehicle_riders',
      label: 'Display Vehicle Riders',
      category: 'vehicles',
      params: [
        {
          name: 'vehicleId',
          label: 'Vehicle',
          type: 'select',
          options: (c) => [...c.state.vehicles.keys()],
        },
      ],
    },
    // Passenger commands
    {
      key: 'set_vehicle_riders',
      label: 'Set Vehicle Riders',
      category: 'passengers',
      params: [
        {
          name: 'vehicleId',
          label: 'Vehicle',
          type: 'select',
          options: (c) => [...c.state.vehicles.keys()],
        },
        { name: 'numRiders', label: 'Num Riders', type: 'number' },
      ],
    },
    {
      key: 'set_waiting_passengers',
      label: 'Set Waiting Passengers',
      category: 'passengers',
      params: [
        {
          name: 'locationId',
          label: 'Location',
          type: 'select',
          options: (c) => [...c.state.locations.keys()],
        },
        { name: 'numWaiting', label: 'Num Waiting', type: 'number' },
      ],
    },
    {
      key: 'set_passenger_ranges',
      label: 'Set Passenger Ranges',
      category: 'passengers',
      params: [
        {
          name: 'locationId',
          label: 'Location',
          type: 'select',
          options: (c) => [...c.state.locations.keys()],
        },
        { name: 'debarkLow', label: 'Debark Low', type: 'number' },
        { name: 'debarkHigh', label: 'Debark High', type: 'number' },
        { name: 'transferLow', label: 'Transfer Low', type: 'number' },
        { name: 'transferHigh', label: 'Transfer High', type: 'number' },
        { name: 'boardLow', label: 'Board Low', type: 'number' },
        { name: 'boardHigh', label: 'Board High', type: 'number' },
      ],
    },
    // Hazard commands
    {
      key: 'create_hazard',
      label: 'Create Hazard',
      category: 'hazards',
      params: [
        { name: 'description', label: 'Description', type: 'text' },
        { name: 'id', label: 'Hazard ID', type: 'text' },
        {
          name: 'type',
          label: 'Type',
          type: 'select',
          options: () => ['short_term', 'long_term'],
        },
        { name: 'impact', label: 'Impact', type: 'number' },
        {
          name: 'location1Id',
          label: 'Location 1',
          type: 'select',
          options: (c) => [...c.state.locations.keys()],
        },
        {
          name: 'location2Id',
          label: 'Location 2 (optional)',
          type: 'select',
          options: (c) => ['', ...c.state.locations.keys()],
        },
      ],
    },
    {
      key: 'display_hazards',
      label: 'Display All Hazards',
      category: 'hazards',
      params: [],
    },
    {
      key: 'display_hazards_at_location',
      label: 'Display Hazards at Location',
      category: 'hazards',
      params: [
        {
          name: 'locationId',
          label: 'Location',
          type: 'select',
          options: (c) => [...c.state.locations.keys()],
        },
      ],
    },
    {
      key: 'remove_hazard',
      label: 'Remove Hazard',
      category: 'hazards',
      params: [
        {
          name: 'hazardId',
          label: 'Hazard',
          type: 'select',
          options: (c) => c.state.hazards.map((h) => h.id),
        },
      ],
    },
  ];

  // derive categories list
  const categories = Array.from(new Set(cmdDefs.map((c) => c.category))).map((key) => {
    const first = cmdDefs.find((c) => c.category === key)!;
    const label = key.charAt(0).toUpperCase() + key.slice(1);
    return { key, label } as { key: string; label: string };
  });

  // ------------------------------ state ----------------------------------
  const [selectedCategory, setSelectedCategory] = useState(categories[0].key);
  const cmdsInCat = cmdDefs.filter((c) => c.category === selectedCategory);
  const [selectedKey, setSelectedKey] = useState(cmdsInCat[0].key);
  const selectedCmd = cmdDefs.find((c) => c.key === selectedKey)!;
  const [paramValues, setParamValues] = useState<Record<string, string>>({});
  const [rawCmd, setRawCmd] = useState('');

  // Reset params when command changes
  const handleCategoryChange = (catKey: string) => {
    setSelectedCategory(catKey);
    const firstCmd = cmdDefs.find((c) => c.category === catKey)!;
    setSelectedKey(firstCmd.key);
    setParamValues({});
  };

  const handleCommandChange = (key: string) => {
    setSelectedKey(key);
    setParamValues({});
  };

  const handleParamChange = (name: string, value: string) => {
    setParamValues((prev) => ({ ...prev, [name]: value }));
  };

  const handleRun = () => {
    if (rawCmd.trim()) {
      onSubmit(rawCmd.trim());
      setRawCmd('');
      return;
    }
    // Special handling for create_hazard command
    if (selectedKey === 'create_hazard') {
      // Check if hazard ID already exists
      const hazardId = (paramValues['id'] ?? '').trim();
      if (controller.state.hazards.some(h => h.id === hazardId)) {
        alert('A hazard with this ID already exists');
        return;
      }

      const params = selectedCmd.params.map(p => {
        const value = (paramValues[p.name] ?? '').trim();
        // Skip location2Id if it's empty
        if (p.name === 'location2Id' && !value) {
          return null;
        }
        return value;
      }).filter(v => v !== null);
      const cmdLine = [selectedKey, ...params].join(',');
      onSubmit(cmdLine);
      return;
    }
    const parts = [selectedKey, ...selectedCmd.params.map((p) => (paramValues[p.name] ?? '').trim())];
    const cmdLine = parts.join(',');
    onSubmit(cmdLine);
  };

  // ------------------------------ rendering ------------------------------
  return (
    <Box component="form" onSubmit={(e) => { e.preventDefault(); handleRun(); }}>
      {/* First row: category, command, raw input, run */}
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems="flex-start" sx={{ mb: 2 }}>
        {/* Category dropdown */}
        <FormControl fullWidth sx={{ minWidth: 160 }}>
          <InputLabel id="cat-select-label">Category</InputLabel>
          <Select
            labelId="cat-select-label"
            value={selectedCategory}
            label="Category"
            onChange={(e) => handleCategoryChange(e.target.value as string)}
          >
            {categories.map((cat) => (
              <MenuItem key={cat.key} value={cat.key}>
                {cat.label}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        {/* Command dropdown */}
        <FormControl fullWidth sx={{ minWidth: 220 }}>
          <InputLabel id="cmd-select-label">Command</InputLabel>
          <Select
            labelId="cmd-select-label"
            value={selectedKey}
            label="Command"
            onChange={(e) => handleCommandChange(e.target.value as string)}
          >
            {cmdsInCat.map((cmd) => (
              <MenuItem key={cmd.key} value={cmd.key}>
                {cmd.label}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        {/* Raw command */}
        <TextField
          fullWidth
          label="CLI Command (optional)"
          value={rawCmd}
          onChange={(e) => setRawCmd(e.target.value)}
          // Enter submits because the whole form has onSubmit handler
          sx={{ minWidth: 300 }}
        />

        <Button variant="contained" type="submit" sx={{ whiteSpace: 'nowrap' }}>
          Run
        </Button>
      </Stack>

      {/* Second row: parameter inputs */}
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} flexWrap="wrap">
        {selectedCmd.params.map((param) => {
          const val = paramValues[param.name] ?? '';
          if (param.type === 'select') {
            const opts = param.options ? param.options(controller) : [];
            return (
              <FormControl key={param.name} sx={{ minWidth: 160 }}>
                <InputLabel id={`${param.name}-label`}>{param.label}</InputLabel>
                <Select
                  labelId={`${param.name}-label`}
                  value={val}
                  label={param.label}
                  onChange={(e) => handleParamChange(param.name, e.target.value as string)}
                >
                  {opts.map((opt) => (
                    <MenuItem key={opt} value={opt}>
                      {opt}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            );
          }
          return (
            <TextField
              key={param.name}
              label={param.label}
              value={val}
              type={param.type === 'number' ? 'number' : 'text'}
              onChange={(e) => handleParamChange(param.name, e.target.value)}
            />
          );
        })}
      </Stack>
    </Box>
  );
}

export default CommandBuilder; 