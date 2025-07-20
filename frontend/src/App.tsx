import { useRef, useState } from 'react';
import Container from '@mui/material/Container';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Divider from '@mui/material/Divider';
import Image from 'mui-image';

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

  const appendLog = (lines: string[]) => {
    setLogLines((prev) => [...prev, ...lines]);
  };

  const handleCommand = (cmd: string) => {
    if (!cmd.trim()) return;
    const out = controllerRef.current.executeCommand(cmd);
    appendLog([`$> ${cmd}`, ...out]);
    // trigger re-render for event list etc.
    setTick((c) => c + 1);
  };

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
        <Box component="img" src={logoImg} sx={{ height: 60 }} />
        <Typography variant="h4" gutterBottom>
          Transit Simulation GUI
        </Typography>
      </Box>

      <CommandBuilder controller={controllerRef.current} onSubmit={handleCommand} />

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
        React GUI for CS2340 Transit Simulation
      </Typography>
    </Container>
  );
}

export default App; 