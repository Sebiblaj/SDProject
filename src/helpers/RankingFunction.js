export function rankingFunction(props, extensions) {
  const RECENCY_ACCESS_WEIGHT = 0.8;
  const RECENCY_CREATED_WEIGHT = 0.3;
  const RECENCY_UPDATED_WEIGHT = 0.6;
  const SIZE_WEIGHT = 0.1;
  const DEPTH_WEIGHT = 0.25;
  const EXTENSION_WEIGHT = 0.6;
  const READABLE_WEIGHT = 1.0;
  const WRITABLE_WEIGHT = 1.5;
  const EXECUTABLE_WEIGHT = 0.1;

  const timeNow = Date.now() / 1000; 

  const creationAge = timeNow - (props.creationtime / 1000);
  const lastModifiedAge = timeNow - (props.lastmodified / 1000);
  const lastAccessedAge = timeNow - (props.lastaccess / 1000);

  const recencyFactor = 1.0 / (1 + creationAge / 3600);
  const lastModifiedFactor = 1.0 / (1 + lastModifiedAge / 3600);
  const accessFactor = 1.0 / (1 + lastAccessedAge / 3600);

  const sizeFactor = Math.min(1.0, props.filesize / (1024.0 * 1024.0));

  const extensionDTO = extensions.find(f => f.type === props.extension);
  const extensionFactor = extensionDTO ? extensionDTO.weight : 0.0;

  const depthFactor = 1.0 / (1 + props.depth * DEPTH_WEIGHT);

  const recencyScore = RECENCY_ACCESS_WEIGHT * accessFactor
                     + RECENCY_CREATED_WEIGHT * recencyFactor
                     + RECENCY_UPDATED_WEIGHT * lastModifiedFactor;

  const sizeScore = SIZE_WEIGHT * sizeFactor;
  const extensionScore = EXTENSION_WEIGHT * extensionFactor;
  const depthScore = DEPTH_WEIGHT * depthFactor;

  let result = recencyScore + sizeScore + extensionScore + depthScore;

  if (props.readable) result *= READABLE_WEIGHT;
  if (props.writable) result *= WRITABLE_WEIGHT;
  if (props.executable) result *= EXECUTABLE_WEIGHT;

  return result;
}
