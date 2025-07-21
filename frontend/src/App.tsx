import { useRef, useState } from 'react';
import Container from '@mui/material/Container';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Divider from '@mui/material/Divider';
import Snackbar from '@mui/material/Snackbar';
import MuiAlert from '@mui/material/Alert';
import FormControlLabel from '@mui/material/FormControlLabel';
import Button from '@mui/material/Button';

// Logo import (relative to project root)
// @ts-ignore
import logoImg from '../../logo.jpg';
import Grid from '@mui/material/Grid';

import CommandBuilder from './components/CommandBuilder';
import OutputLog from './components/OutputLog';
import EventList from './components/EventList';
import MapPanel from './components/MapPanel';
import { TravelController } from './simulation';

function App() {
  const controllerRef = useRef(new TravelController());
  const [logLines, setLogLines] = useState<string[]>([]);
  const [, setTick] = useState(0); // force re-render when store changes
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const appendLog = (lines: string[]) => {
    setLogLines((prev) => [...prev, ...lines]);
    const err = lines.find((l) => l.startsWith('ERROR:'));
    if (err) {
      setErrorMsg(err.replace(/^ERROR:\s*/, ''));
    }
  };

  const handleCommand = (cmd: string) => {
    if (!cmd.trim()) return;
    const out = controllerRef.current.executeCommand(cmd);
    appendLog([`$> ${cmd}`, ...out]);
    // trigger re-render for event list etc.
    setTick((c) => c + 1);
  };

  const sampleCommands = [
    'create_route,line1,S-Bahn,line',
    'create_route,line2,S-Bahn,line',
    'create_route,line3,S-Bahn,line',
    'create_route,ring1,S-Bahn,ring',
    'create_route,ring2,S-Bahn,ring',
    'create_route,ring3,S-Bahn,ring',
    'create_location,Stadium,Stadium,0,5',
    'create_location,West,West,1,3',
    'create_location,Castle,Castle,2,0',
    'create_location,Bahnhof,Bahnhof,4,5',
    'create_location,Museum,Museum,5,3',
    'create_location,South,South,6,1',
    'create_location,Port,Port,3,10',
    'create_location,North,North,5,8',
    'create_location,Tower,Tower,7,4',
    'create_location,East,East,8,4',
    'create_location,Airport,Airport,10,2',
    'create_location,Gate,Gate,9,9',
    // line1 stops
    'add_location_to_route,line1,Stadium,0',
    'add_location_to_route,line1,West,1',
    'add_location_to_route,line1,Bahnhof,2',
    'add_location_to_route,line1,Tower,3',
    'add_location_to_route,line1,East,4',
    'add_location_to_route,line1,Airport,5',
    'add_location_to_route,line1,East,6',
    'add_location_to_route,line1,Tower,7',
    'add_location_to_route,line1,Bahnhof,8',
    'add_location_to_route,line1,West,9',
    // line2 stops
    'add_location_to_route,line2,Port,0',
    'add_location_to_route,line2,North,1',
    'add_location_to_route,line2,Bahnhof,2',
    'add_location_to_route,line2,West,3',
    'add_location_to_route,line2,Castle,4',
    'add_location_to_route,line2,West,5',
    'add_location_to_route,line2,Bahnhof,6',
    'add_location_to_route,line2,North,7',
    // line3 stops
    'add_location_to_route,line3,Gate,0',
    'add_location_to_route,line3,North,1',
    'add_location_to_route,line3,Bahnhof,2',
    'add_location_to_route,line3,Tower,3',
    'add_location_to_route,line3,South,4',
    'add_location_to_route,line3,Tower,5',
    'add_location_to_route,line3,Bahnhof,6',
    'add_location_to_route,line3,North,7',
    // rings
    'add_location_to_route,ring1,West,0',
    'add_location_to_route,ring1,Bahnhof,1',
    'add_location_to_route,ring1,Tower,2',
    'add_location_to_route,ring1,South,3',
    'add_location_to_route,ring1,Castle,4',
    'add_location_to_route,ring2,Bahnhof,0',
    'add_location_to_route,ring2,Tower,1',
    'add_location_to_route,ring2,Museum,2',
    'add_location_to_route,ring3,Bahnhof,0',
    'add_location_to_route,ring3,North,1',
    'add_location_to_route,ring3,Port,2',
    'add_location_to_route,ring3,North,3',
    'add_location_to_route,ring3,Gate,4',
    // vehicles
    'create_vehicle,100,S-Bahn,1,line1,Stadium,5',
    'create_vehicle,100,S-Bahn,2,line2,Port,5',
    'create_vehicle,100,S-Bahn,3,line3,Gate,5',
    'create_vehicle,100,S-Bahn,4,ring1,West,5',
    'create_vehicle,100,S-Bahn,5,ring2,Bahnhof,5',
    'create_vehicle,100,S-Bahn,6,ring3,Bahnhof,5',
    // riders
    'set_vehicle_riders,1,20',
    'set_vehicle_riders,2,30',
    'set_vehicle_riders,3,20',
    'set_vehicle_riders,4,30',
    'set_vehicle_riders,5,20',
    'set_vehicle_riders,6,30',
    // waiting passengers
    'set_waiting_passengers,Stadium,100',
    'set_waiting_passengers,West,80',
    'set_waiting_passengers,Bahnhof,80',
    'set_waiting_passengers,Castle,150',
    'set_waiting_passengers,Museum,100',
    'set_waiting_passengers,South,80',
    'set_waiting_passengers,Port,30',
    'set_waiting_passengers,North,80',
    'set_waiting_passengers,Tower,50',
    'set_waiting_passengers,East,90',
    'set_waiting_passengers,Airport,80',
    'set_waiting_passengers,Gate,100',

    // passenger ranges
    'set_passenger_ranges,Stadium,0,30,0,30,0,30',
    'set_passenger_ranges,West,0,30,0,30,0,30',
    'set_passenger_ranges,Castle,0,30,0,30,0,30',
    'set_passenger_ranges,Bahnhof,0,30,0,30,0,30',
    'set_passenger_ranges,Museum,0,30,0,30,0,30',
    'set_passenger_ranges,South,0,30,0,30,0,30',
    'set_passenger_ranges,Port,0,30,0,30,0,30',
    'set_passenger_ranges,North,0,30,0,30,0,30',
    'set_passenger_ranges,Tower,0,30,0,30,0,30',
    'set_passenger_ranges,East,0,30,0,30,0,30',
    'set_passenger_ranges,Airport,0,30,0,30,0,30',
    'set_passenger_ranges,Gate,0,30,0,30,0,30',
  ];

  const loadSample = () => {
    sampleCommands.forEach((cmd) => {
      const out = controllerRef.current.executeCommand(cmd);
      appendLog([`$> ${cmd}`, ...out]);
    });
    setTick((c) => c + 1);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
        <Box component="img" src={logoImg} sx={{ height: 60 }} />
        <Typography variant="h4" gutterBottom>
          Mass Transit Simulation
        </Typography>

        <Box sx={{ marginLeft: 'auto' }}>
          <Typography variant="subtitle1" color="textSecondary">
            Time: {controllerRef.current.state.time}
          </Typography>
        </Box>
      </Box>

      <CommandBuilder controller={controllerRef.current} onSubmit={handleCommand} />
      <Box sx={{ mt: 2 }}>
        <Button variant="contained" onClick={loadSample}>Load Initial State</Button>
      </Box>

      <Box sx={{ my: 3 }}>
        <Grid container spacing={4}>
          <Grid item xs={12} md={6}>
            <Typography variant="h6" gutterBottom>
              Output Log
            </Typography>
            <OutputLog lines={logLines} />
          </Grid>

          <Grid item xs={12} md={6}>
            <Typography variant="h6" gutterBottom>
              Upcoming Events
            </Typography>
            <EventList controller={controllerRef.current} />
          </Grid>
        </Grid>
      </Box>

      <Box sx={{ my: 4 }}>
        <Typography variant="h6" gutterBottom>
          Network Map
        </Typography>
        <MapPanel controller={controllerRef.current} />
      </Box>

      <Divider sx={{ my: 4 }} />

      <Typography variant="caption" color="text.secondary">
        CS2340 Team 10 Transit Simulation
      </Typography>

      {/* Error notification */}
      <Snackbar
        open={Boolean(errorMsg)}
        autoHideDuration={6000}
        onClose={() => setErrorMsg(null)}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <MuiAlert severity="error" onClose={() => setErrorMsg(null)} sx={{ width: '100%' }}>
          {errorMsg}
        </MuiAlert>
      </Snackbar>
    </Container>
  );
}

export default App; 