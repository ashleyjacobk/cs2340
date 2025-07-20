import Paper from '@mui/material/Paper';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import { TravelController } from '../simulation';

interface Props {
  controller: TravelController;
  max?: number;
}

function EventList({ controller, max = 10 }: Props) {
  const events = controller.peekNextEvents(max);
  return (
    <Paper variant="outlined" sx={{ p: 0, height: 400, overflow: 'auto' }}>
      <List dense>
        {events.map((e, idx) => (
          <ListItem key={idx} disableGutters>
            <ListItemText primaryTypographyProps={{ sx: { fontFamily: 'monospace' } }} primary={e} />
          </ListItem>
        ))}
        {events.length === 0 && (
          <ListItem>
            <ListItemText primary="No scheduled events" />
          </ListItem>
        )}
      </List>
    </Paper>
  );
}

export default EventList; 