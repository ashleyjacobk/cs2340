import { useRef, useState } from 'react';
import Paper from '@mui/material/Paper';
import Popover from '@mui/material/Popover';
import Typography from '@mui/material/Typography';
import { TravelController } from '../simulation';
import Box from '@mui/material/Box';

interface Props {
  controller: TravelController;
  width?: number;
  height?: number;
}

function MapPanel({ controller, width = 600, height = 400 }: Props) {
  const containerRef = useRef<HTMLDivElement | null>(null);

  // Gather data
  const locs = Array.from(controller.state.locations.values());
  const vehicles = Array.from(controller.state.vehicles.values());
  const routes = Array.from(controller.state.routes.values());

  // Compute bounds
  const xs = locs.map((l) => l.x);
  const ys = locs.map((l) => l.y);
  const minXRaw = Math.min(...xs, 0);
  const maxXRaw = Math.max(...xs, 1);
  const minYRaw = Math.min(...ys, 0);
  const maxYRaw = Math.max(...ys, 1);

  const padding = 20;

  // Add 5% buffer around extents (or at least 1 unit)
  const bufferX = Math.max((maxXRaw - minXRaw) * 0.05, 1);
  const bufferY = Math.max((maxYRaw - minYRaw) * 0.05, 1);

  const minX = minXRaw - bufferX;
  const maxX = maxXRaw + bufferX;
  const minY = minYRaw - bufferY;
  const maxY = maxYRaw + bufferY;

  const viewW = maxX - minX || 1;
  const viewH = maxY - minY || 1;

  const scaleX = (width - 2 * padding) / viewW;
  const scaleY = (height - 2 * padding) / viewH;

  // Determine grid step (1, 2, 5, 10 ...) based on range
  const bestStep = (range: number) => {
    const pow10 = Math.pow(10, Math.floor(Math.log10(range)));
    const candidates = [1, 2, 5, 10];
    for (const c of candidates) {
      const step = c * pow10;
      if (range / step <= 10) return step;
    }
    return pow10 * 10;
  };

  const stepX = bestStep(viewW);
  const stepY = bestStep(viewH);

  const gridLinesX: number[] = [];
  for (let x = Math.ceil(minX / stepX) * stepX; x <= maxX; x += stepX) {
    gridLinesX.push(x);
  }
  const gridLinesY: number[] = [];
  for (let y = Math.ceil(minY / stepY) * stepY; y <= maxY; y += stepY) {
    gridLinesY.push(y);
  }

  const mapX = (x: number) => padding + (x - minX) * scaleX;
  const mapY = (y: number) => padding + (maxY - y) * scaleY; // invert Y-axis with padded maxY

  // ---------------- hover popover ----------------
  type HoverInfo =
    | { kind: 'loc'; loc: ReturnType<TravelController['state']['locations']['get']>; x: number; y: number }
    | { kind: 'route'; route: ReturnType<TravelController['state']['routes']['get']>; x: number; y: number };

  const [hover, setHover] = useState<HoverInfo | null>(null);

  return (
    <Paper
      ref={containerRef}
      variant="outlined"
      sx={{ position: 'relative', width: '100%', height }}
    >
      <svg width="100%" height="100%" viewBox={`0 0 ${width} ${height}`} preserveAspectRatio="xMidYMid meet" font-family="monospace" font-size="8">
        {/* Grid lines Y (horizontal) */}
        {gridLinesY.map((gy) => (
          <g key={`hy-${gy}`}>
            <line
              x1={padding}
              x2={width - padding}
              y1={mapY(gy)}
              y2={mapY(gy)}
              stroke="#e0e0e0"
              strokeWidth={0.5}
            />
            <text x={padding - 4} y={mapY(gy) + 3} textAnchor="end" fill="#888">
              {gy}
            </text>
          </g>
        ))}

        {/* Grid lines X (vertical) */}
        {gridLinesX.map((gx) => (
          <g key={`vx-${gx}`}>
            <line
              y1={padding}
              y2={height - padding}
              x1={mapX(gx)}
              x2={mapX(gx)}
              stroke="#e0e0e0"
              strokeWidth={0.5}
            />
            <text y={height - padding + 10} x={mapX(gx)} textAnchor="middle" fill="#888">
              {gx}
            </text>
          </g>
        ))}

        {/* Routes */}
        {routes.map((route) => {
          const locsInRoute = route.getLocations();
          if (locsInRoute.length < 2) return null;
          const pathParts = locsInRoute
            .map((l, idx) => `${idx === 0 ? 'M' : 'L'} ${mapX(l.x)} ${mapY(l.y)}`);
          // close loop: add line back to first location
          const firstLoc = locsInRoute[0];
          pathParts.push(`L ${mapX(firstLoc.x)} ${mapY(firstLoc.y)}`);
          const pathD = pathParts.join(' ');
          return (
            <path
              key={route.id}
              d={pathD}
              fill="none"
              stroke="#999"
              strokeWidth={1}
              style={{ pointerEvents: 'all' }}
              onMouseEnter={(e) => setHover({ kind: 'route', route, x: e.clientX, y: e.clientY })}
              onMouseMove={(e) => setHover({ kind: 'route', route, x: e.clientX, y: e.clientY })}
              onMouseLeave={() => setHover(null)}
            />
          );
        })}

        {/* Locations */}
        {locs.map((loc) => (
          <g key={loc.id}
            style={{ pointerEvents: 'all' }}
            onMouseEnter={(e) => setHover({ kind: 'loc', loc, x: e.clientX, y: e.clientY })}
            onMouseMove={(e) => setHover({ kind: 'loc', loc, x: e.clientX, y: e.clientY })}
            onMouseLeave={() => setHover(null)}>
            <circle cx={mapX(loc.x)} cy={mapY(loc.y)} r={4} fill="#1976d2" />
            <text x={mapX(loc.x) + 6} y={mapY(loc.y) - 6} fontSize={10} fill="#333">
              {loc.id}
            </text>
          </g>
        ))}

        {/* Vehicles */}
        {vehicles.map((v) => (
          <rect
            key={v.id}
            x={mapX(v.currentLocation.x) - 4}
            y={mapY(v.currentLocation.y) - 4}
            width={8}
            height={8}
            fill="#dc004e"
          >
            <title>{v.id}</title>
          </rect>
        ))}
      </svg>

      {/* Popover */}
      <Popover
        open={Boolean(hover)}
        anchorReference="anchorPosition"
        anchorPosition={hover ? { top: hover.y, left: hover.x } : undefined}
        onClose={() => setHover(null)}
        disableRestoreFocus
      >
        {hover && hover.kind === 'loc' && (
          <Box sx={{ p: 2, maxWidth: 300 }}>
            <Typography variant="subtitle1" fontWeight="bold">
              {hover.loc.name} ({hover.loc.id})
            </Typography>
            <Typography variant="body2">
              Waiting: {hover.loc.waitingPassengers}
            </Typography>
            <Typography variant="body2" sx={{ mt: 0.5 }}>
              Vehicles:
            </Typography>
            <Typography variant="caption">
              {vehicles
                .filter((v) => v.currentLocation && v.currentLocation.id === hover.loc.id)
                .map((v) => `${v.id} (${v.getCurrentPassengers()}/${v.capacity})`)
                .join(', ') || 'None'}
            </Typography>
            <Typography variant="body2" sx={{ mt: 0.5 }}>
              Routes: {routes.filter((r) => r.getLocations().includes(hover.loc)).map((r) => r.id).join(', ') || 'None'}
            </Typography>
          </Box>
        )}
        {hover && hover.kind === 'route' && (
          <Box sx={{ p: 2 }}>
            <Typography variant="subtitle1" fontWeight="bold">
              Route {hover.route.id}
            </Typography>
            <Typography variant="body2">Type: {hover.route.vehicleType}</Typography>
            <Typography variant="caption">
              Stops: {hover.route.getLocations().map((l) => l.name).join(' → ')}
            </Typography>
          </Box>
        )}
      </Popover>
    </Paper>
  );
}

export default MapPanel; 