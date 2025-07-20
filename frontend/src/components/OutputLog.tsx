import Paper from '@mui/material/Paper';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import { useEffect, useRef } from 'react';

interface Props {
  lines: string[];
}

function OutputLog({ lines }: Props) {
  const listRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const div = listRef.current;
    if (div) {
      div.scrollTop = div.scrollHeight;
    }
  }, [lines]);

  return (
    <Paper variant="outlined" sx={{ p: 0, height: 400, overflow: 'auto' }} ref={listRef}>
      <List dense>
        {lines.map((line, idx) => (
          <ListItem key={idx} disableGutters>
            <ListItemText primaryTypographyProps={{ sx: { fontFamily: 'monospace' } }} primary={line} />
          </ListItem>
        ))}
      </List>
    </Paper>
  );
}

export default OutputLog; 