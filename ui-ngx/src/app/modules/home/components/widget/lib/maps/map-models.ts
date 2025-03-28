

import { Datasource, FormattedData } from '@app/shared/models/widget.models';
import tinycolor from 'tinycolor2';
import { BaseIconOptions, Icon } from 'leaflet';
import { Observable } from 'rxjs';

export const DEFAULT_MAP_PAGE_SIZE = 16384;
export const DEFAULT_ZOOM_LEVEL = 8;

export type MarkerImageInfo = {
  url: string;
  size: number;
  markerOffset?: [number, number];
  tooltipOffset?: [number, number];
};

export type MarkerIconInfo = {
  icon: Icon<BaseIconOptions>;
  size: [number, number];
};

export interface MapImage {
  imageUrl: string;
  aspect: number;
  update?: boolean;
}

export type actionsHandler = ($event: Event, datasource: Datasource) => void;

export interface CircleData {
  latitude: number;
  longitude: number;
  radius: number;
}

export type GenericFunction = (data: FormattedData, dsData: FormattedData[], dsIndex: number) => string;
export type MarkerImageFunction = (data: FormattedData, markerImages: string[],
                                   dsData: FormattedData[], dsIndex: number) => MarkerImageInfo;
export type PosFunction = (origXPos, origYPos, data: FormattedData,
                           dsData: FormattedData[], dsIndex: number, aspect: number) => { x: number, y: number };
export type MarkerIconReadyFunction = (icon: MarkerIconInfo) => void;

export enum GoogleMapType {
  roadmap = 'roadmap',
  satellite = 'satellite',
  hybrid = 'hybrid',
  terrain = 'terrain'
}

export const googleMapTypeProviderTranslationMap = new Map<GoogleMapType, string>(
  [
    [GoogleMapType.roadmap, 'widgets.maps.google-map-type-roadmap'],
    [GoogleMapType.satellite, 'widgets.maps.google-map-type-satelite'],
    [GoogleMapType.hybrid, 'widgets.maps.google-map-type-hybrid'],
    [GoogleMapType.terrain, 'widgets.maps.google-map-type-terrain']
  ]
);

export interface GoogleMapProviderSettings {
  gmApiKey: string;
  gmDefaultMapType: GoogleMapType;
}

export const defaultGoogleMapProviderSettings: GoogleMapProviderSettings = {
  gmApiKey: 'AIzaSyDoEx2kaGz3PxwbI9T7ccTSg5xjdw8Nw8Q',
  gmDefaultMapType: GoogleMapType.roadmap
};

export enum OpenStreetMapProvider {
  openStreetMapnik = 'OpenStreetMap.Mapnik',
  openStreetHot = 'OpenStreetMap.HOT',
  esriWorldStreetMap = 'Esri.WorldStreetMap',
  esriWorldTopoMap = 'Esri.WorldTopoMap',
  esriWorldImagery = 'Esri.WorldImagery',
  cartoDbPositron = 'CartoDB.Positron',
  cartoDbDarkMatter = 'CartoDB.DarkMatter'
}

export const openStreetMapProviderTranslationMap = new Map<OpenStreetMapProvider, string>(
  [
    [OpenStreetMapProvider.openStreetMapnik, 'widgets.maps.openstreet-provider-mapnik'],
    [OpenStreetMapProvider.openStreetHot, 'widgets.maps.openstreet-provider-hot'],
    [OpenStreetMapProvider.esriWorldStreetMap, 'widgets.maps.openstreet-provider-esri-street'],
    [OpenStreetMapProvider.esriWorldTopoMap, 'widgets.maps.openstreet-provider-esri-topo'],
    [OpenStreetMapProvider.esriWorldImagery, 'widgets.maps.openstreet-provider-esri-imagery'],
    [OpenStreetMapProvider.cartoDbPositron, 'widgets.maps.openstreet-provider-cartodb-positron'],
    [OpenStreetMapProvider.cartoDbDarkMatter, 'widgets.maps.openstreet-provider-cartodb-dark-matter']
  ]
);

export interface OpenStreetMapProviderSettings {
  mapProvider: OpenStreetMapProvider;
  useCustomProvider: boolean;
  customProviderTileUrl?: string;
}

export const defaultOpenStreetMapProviderSettings: OpenStreetMapProviderSettings = {
  mapProvider: OpenStreetMapProvider.openStreetMapnik,
  useCustomProvider: false,
  customProviderTileUrl: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'
};

export enum HereMapProvider {
  hereNormalDay = 'HERE.normalDay',
  hereNormalNight = 'HERE.normalNight',
  hereHybridDay = 'HERE.hybridDay',
  hereTerrainDay = 'HERE.terrainDay'
}

export const hereMapProviderTranslationMap = new Map<HereMapProvider, string>(
  [
    [HereMapProvider.hereNormalDay, 'widgets.maps.here-map-normal-day'],
    [HereMapProvider.hereNormalNight, 'widgets.maps.here-map-normal-night'],
    [HereMapProvider.hereHybridDay, 'widgets.maps.here-map-hybrid-day'],
    [HereMapProvider.hereTerrainDay, 'widgets.maps.here-map-terrain-day']
  ]
);

export interface HereMapProviderSettings {
  mapProviderHere: HereMapProvider;
  credentials: {
    useV3: boolean;
    app_id: string;
    app_code: string;
    apiKey: string;
  };
}

export const defaultHereMapProviderSettings: HereMapProviderSettings = {
  mapProviderHere: HereMapProvider.hereNormalDay,
  credentials: {
    useV3: true,
    app_id: 'AhM6TzD9ThyK78CT3ptx',
    app_code: 'p6NPiITB3Vv0GMUFnkLOOg',
    apiKey: 'kVXykxAfZ6LS4EbCTO02soFVfjA7HoBzNVVH9u7nzoE'
  }
};

export interface ImageMapProviderSettings {
  mapImageUrl?: string;
  imageEntityAlias?: string;
  imageUrlAttribute?: string;
}

export const defaultImageMapProviderSettings: ImageMapProviderSettings = {
  mapImageUrl: 'data:image/svg+xml;base64,PHN2ZyBpZD0ic3ZnMiIgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIGhlaWdodD0iMTAwIiB3aWR0aD0iMTAwIiB2ZXJzaW9uPSIxLjEiIHhtbG5zOmNjPSJodHRwOi8vY3JlYXRpdmVjb21tb25zLm9yZy9ucyMiIHhtbG5zOmRjPSJodHRwOi8vcHVybC5vcmcvZGMvZWxlbWVudHMvMS4xLyIgdmlld0JveD0iMCAwIDEwMCAxMDAiPgogPGcgaWQ9ImxheWVyMSIgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMCAtOTUyLjM2KSI+CiAgPHJlY3QgaWQ9InJlY3Q0Njg0IiBzdHJva2UtbGluZWpvaW49InJvdW5kIiBoZWlnaHQ9Ijk5LjAxIiB3aWR0aD0iOTkuMDEiIHN0cm9rZT0iIzAwMCIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiB5PSI5NTIuODYiIHg9Ii40OTUwNSIgc3Ryb2tlLXdpZHRoPSIuOTkwMTAiIGZpbGw9IiNlZWUiLz4KICA8dGV4dCBpZD0idGV4dDQ2ODYiIHN0eWxlPSJ3b3JkLXNwYWNpbmc6MHB4O2xldHRlci1zcGFjaW5nOjBweDt0ZXh0LWFuY2hvcjptaWRkbGU7dGV4dC1hbGlnbjpjZW50ZXIiIGZvbnQtd2VpZ2h0PSJib2xkIiB4bWw6c3BhY2U9InByZXNlcnZlIiBmb250LXNpemU9IjEwcHgiIGxpbmUtaGVpZ2h0PSIxMjUlIiB5PSI5NzAuNzI4MDkiIHg9IjQ5LjM5NjQ3NyIgZm9udC1mYW1pbHk9IlJvYm90byIgZmlsbD0iIzY2NjY2NiI+PHRzcGFuIGlkPSJ0c3BhbjQ2OTAiIHg9IjUwLjY0NjQ3NyIgeT0iOTcwLjcyODA5Ij5JbWFnZSBiYWNrZ3JvdW5kIDwvdHNwYW4+PHRzcGFuIGlkPSJ0c3BhbjQ2OTIiIHg9IjQ5LjM5NjQ3NyIgeT0iOTgzLjIyODA5Ij5pcyBub3QgY29uZmlndXJlZDwvdHNwYW4+PC90ZXh0PgogIDxyZWN0IGlkPSJyZWN0NDY5NCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCIgaGVpZ2h0PSIxOS4zNiIgd2lkdGg9IjY5LjM2IiBzdHJva2U9IiMwMDAiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgeT0iOTkyLjY4IiB4PSIxNS4zMiIgc3Ryb2tlLXdpZHRoPSIuNjM5ODYiIGZpbGw9Im5vbmUiLz4KIDwvZz4KPC9zdmc+Cg==',
  imageEntityAlias: '',
  imageUrlAttribute: ''
};

export enum TencentMapType {
  roadmap = 'roadmap',
  satellite = 'satellite',
  hybrid = 'hybrid'
}

export const tencentMapTypeProviderTranslationMap = new Map<TencentMapType, string>(
  [
    [TencentMapType.roadmap, 'widgets.maps.tencent-map-type-roadmap'],
    [TencentMapType.satellite, 'widgets.maps.tencent-map-type-satelite'],
    [TencentMapType.hybrid, 'widgets.maps.tencent-map-type-hybrid']
  ]
);

export interface TencentMapProviderSettings {
  tmApiKey: string;
  tmDefaultMapType: TencentMapType;
}

export const defaultTencentMapProviderSettings: TencentMapProviderSettings = {
  tmApiKey: '84d6d83e0e51e481e50454ccbe8986b',
  tmDefaultMapType: TencentMapType.roadmap
};

export enum MapProviders {
  google = 'google-map',
  openstreet = 'openstreet-map',
  here = 'here',
  image = 'image-map',
  tencent = 'tencent-map'
}

export const mapProviderTranslationMap = new Map<MapProviders, string>(
  [
    [MapProviders.google, 'widgets.maps.map-provider-google'],
    [MapProviders.openstreet, 'widgets.maps.map-provider-openstreet'],
    [MapProviders.here, 'widgets.maps.map-provider-here'],
    [MapProviders.image, 'widgets.maps.map-provider-image'],
    [MapProviders.tencent, 'widgets.maps.map-provider-tencent']
  ]
);

export interface MapProviderSettings extends GoogleMapProviderSettings, OpenStreetMapProviderSettings,
                                             HereMapProviderSettings, ImageMapProviderSettings, TencentMapProviderSettings {
  provider: MapProviders;
}

export const defaultMapProviderSettings: MapProviderSettings = {
  provider: MapProviders.openstreet,
  ...defaultGoogleMapProviderSettings,
  ...defaultOpenStreetMapProviderSettings,
  ...defaultHereMapProviderSettings,
  ...defaultImageMapProviderSettings,
  ...defaultTencentMapProviderSettings
};

export interface CommonMapSettings {
  latKeyName: string;
  lngKeyName: string;
  xPosKeyName: string;
  yPosKeyName: string;
  defaultZoomLevel: number;
  defaultCenterPosition?: string;
  disableScrollZooming: boolean;
  disableDoubleClickZooming: boolean;
  disableZoomControl: boolean;
  fitMapBounds: boolean;
  useDefaultCenterPosition: boolean;
  mapPageSize: number;
}

export interface WidgetCommonMapSettings extends CommonMapSettings {
  parsedDefaultCenterPosition: [number, number];
  minZoomLevel: number;
}

export interface WidgetToolipSettings {
  tooltipAction: { [name: string]: actionsHandler };
}

export const defaultCommonMapSettings: CommonMapSettings = {
  latKeyName: 'latitude',
  lngKeyName: 'longitude',
  xPosKeyName: 'xPos',
  yPosKeyName: 'yPos',
  defaultZoomLevel: null,
  defaultCenterPosition: '0,0',
  disableScrollZooming: false,
  disableDoubleClickZooming: false,
  disableZoomControl: false,
  fitMapBounds: true,
  useDefaultCenterPosition: false,
  mapPageSize: DEFAULT_MAP_PAGE_SIZE
};

export interface TripAnimationCommonSettings {
  normalizationStep: number;
  latKeyName: string;
  lngKeyName: string;
  showTooltip: boolean;
  tooltipColor: string;
  tooltipFontColor: string;
  tooltipOpacity: number;
  useTooltipFunction: boolean;
  tooltipPattern?: string;
  tooltipFunction?: string;
  autocloseTooltip: boolean;
}

export interface WidgetTripAnimationCommonSettings extends TripAnimationCommonSettings {
  parsedTooltipFunction: GenericFunction;
}

export const defaultTripAnimationCommonSettings: TripAnimationCommonSettings = {
  normalizationStep: 1000,
  latKeyName: 'latitude',
  lngKeyName: 'longitude',
  showTooltip: true,
  tooltipColor: '#fff',
  tooltipFontColor: '#000',
  tooltipOpacity: 1,
  useTooltipFunction: false,
  tooltipPattern: '<b>${entityName}</b><br/><br/><b>Latitude:</b> ${latitude:7}<br/><b>Longitude:</b> ${longitude:7}',
  tooltipFunction: null,
  autocloseTooltip: true
};

export enum ShowTooltipAction {
  click = 'click',
  hover = 'hover'
}

export const showTooltipActionTranslationMap = new Map<ShowTooltipAction, string>(
  [
    [ShowTooltipAction.click, 'widgets.maps.show-tooltip-action-click'],
    [ShowTooltipAction.hover, 'widgets.maps.show-tooltip-action-hover']
  ]
);

export interface MarkersSettings {
  markerOffsetX: number;
  markerOffsetY: number;
  posFunction?: string;
  draggableMarker: boolean;
  showLabel: boolean;
  useLabelFunction: boolean;
  label?: string;
  labelFunction?: string;
  showTooltip: boolean;
  showTooltipAction: ShowTooltipAction;
  autocloseTooltip: boolean;
  useTooltipFunction: boolean;
  tooltipPattern?: string;
  tooltipFunction?: string;
  tooltipOffsetX: number;
  tooltipOffsetY: number;
  color?: string;
  useColorFunction: boolean;
  colorFunction?: string;
  useMarkerImageFunction: boolean;
  markerImage?: string;
  markerImageSize?: number;
  markerImageFunction?: string;
  markerImages?: string[];
}

export interface WidgetMarkersSettings extends MarkersSettings, WidgetToolipSettings {
  parsedLabelFunction: GenericFunction;
  parsedTooltipFunction: GenericFunction;
  parsedColorFunction: GenericFunction;
  parsedMarkerImageFunction: MarkerImageFunction;
  markerClick: { [name: string]: actionsHandler };
  currentImage: MarkerImageInfo;
  tinyColor: tinycolor.Instance;
  icon: MarkerIconInfo;
  icon$?: Observable<MarkerIconInfo>;
}

export const defaultMarkersSettings: MarkersSettings = {
  markerOffsetX: 0.5,
  markerOffsetY: 1,
  posFunction: 'return {x: origXPos, y: origYPos};',
  draggableMarker: false,
  showLabel: true,
  useLabelFunction: false,
  label: '${entityName}',
  labelFunction: null,
  showTooltip: true,
  showTooltipAction: ShowTooltipAction.click,
  autocloseTooltip: true,
  useTooltipFunction: false,
  tooltipPattern: '<b>${entityName}</b><br/><br/><b>Latitude:</b> ${latitude:7}<br/><b>Longitude:</b> ${longitude:7}',
  tooltipFunction: null,
  tooltipOffsetX: 0,
  tooltipOffsetY: -1,
  color: '#FE7569',
  useColorFunction: false,
  colorFunction: null,
  useMarkerImageFunction: false,
  markerImage: null,
  markerImageSize: 34,
  markerImageFunction: null,
  markerImages: []
};

export interface TripAnimationMarkerSettings {
  rotationAngle: number;
  showLabel: boolean;
  useLabelFunction: boolean;
  label?: string;
  labelFunction?: string;
  useMarkerImageFunction: boolean;
  markerImage?: string;
  markerImageSize?: number;
  markerImageFunction?: string;
  markerImages?: string[];
}

export interface WidgetTripAnimationMarkerSettings extends TripAnimationMarkerSettings {
  parsedLabelFunction: GenericFunction;
  parsedMarkerImageFunction: MarkerImageFunction;
}

export const defaultTripAnimationMarkersSettings: TripAnimationMarkerSettings = {
  rotationAngle: 0,
  showLabel: true,
  useLabelFunction: false,
  label: '${entityName}',
  labelFunction: null,
  useMarkerImageFunction: false,
  markerImage: null,
  markerImageSize: 34,
  markerImageFunction: null,
  markerImages: []
};

export interface PolygonSettings {
  showPolygon: boolean;
  polygonKeyName: string;
  editablePolygon: boolean;
  showPolygonLabel: boolean;
  usePolygonLabelFunction: boolean;
  polygonLabel?: string;
  polygonLabelFunction?: string;
  showPolygonTooltip: boolean;
  showPolygonTooltipAction: ShowTooltipAction;
  autoClosePolygonTooltip: boolean;
  usePolygonTooltipFunction: boolean;
  polygonTooltipPattern?: string;
  polygonTooltipFunction?: string;
  polygonColor?: string;
  polygonOpacity?: number;
  usePolygonColorFunction: boolean;
  polygonColorFunction?: string;
  polygonStrokeColor?: string;
  polygonStrokeOpacity?: number;
  polygonStrokeWeight?: number;
  usePolygonStrokeColorFunction: boolean;
  polygonStrokeColorFunction?: string;
}

export interface WidgetPolygonSettings extends PolygonSettings, WidgetToolipSettings {
  parsedPolygonLabelFunction: GenericFunction;
  parsedPolygonTooltipFunction: GenericFunction;
  parsedPolygonColorFunction: GenericFunction;
  parsedPolygonStrokeColorFunction: GenericFunction;
  polygonClick: { [name: string]: actionsHandler };
}

export const defaultPolygonSettings: PolygonSettings = {
  showPolygon: false,
  polygonKeyName: 'perimeter',
  editablePolygon: false,
  showPolygonLabel: false,
  usePolygonLabelFunction: false,
  polygonLabel: '${entityName}',
  polygonLabelFunction: null,
  showPolygonTooltip: false,
  showPolygonTooltipAction: ShowTooltipAction.click,
  autoClosePolygonTooltip: true,
  usePolygonTooltipFunction: false,
  polygonTooltipPattern: '<b>${entityName}</b><br/><br/><b>TimeStamp:</b> ${ts:7}',
  polygonTooltipFunction: null,
  polygonColor: '#3388ff',
  polygonOpacity: 0.2,
  usePolygonColorFunction: false,
  polygonColorFunction: null,
  polygonStrokeColor: '#3388ff',
  polygonStrokeOpacity: 1,
  polygonStrokeWeight: 3,
  usePolygonStrokeColorFunction: false,
  polygonStrokeColorFunction: null
};

export interface CircleSettings {
  showCircle: boolean;
  circleKeyName: string;
  editableCircle: boolean;
  showCircleLabel: boolean;
  useCircleLabelFunction: boolean;
  circleLabel?: string;
  circleLabelFunction?: string;
  showCircleTooltip: boolean;
  showCircleTooltipAction: ShowTooltipAction;
  autoCloseCircleTooltip: boolean;
  useCircleTooltipFunction: boolean;
  circleTooltipPattern?: string;
  circleTooltipFunction?: string;
  circleFillColor?: string;
  circleFillColorOpacity?: number;
  useCircleFillColorFunction: boolean;
  circleFillColorFunction?: string;
  circleStrokeColor?: string;
  circleStrokeOpacity?: number;
  circleStrokeWeight?: number;
  useCircleStrokeColorFunction: boolean;
  circleStrokeColorFunction?: string;
}

export interface WidgetCircleSettings extends CircleSettings, WidgetToolipSettings {
  parsedCircleLabelFunction: GenericFunction;
  parsedCircleTooltipFunction: GenericFunction;
  parsedCircleFillColorFunction: GenericFunction;
  parsedCircleStrokeColorFunction: GenericFunction;
  circleClick: { [name: string]: actionsHandler };
}

export const defaultCircleSettings: CircleSettings = {
  showCircle: false,
  circleKeyName: 'perimeter',
  editableCircle: false,
  showCircleLabel: false,
  useCircleLabelFunction: false,
  circleLabel: '${entityName}',
  circleLabelFunction: null,
  showCircleTooltip: false,
  showCircleTooltipAction: ShowTooltipAction.click,
  autoCloseCircleTooltip: true,
  useCircleTooltipFunction: false,
  circleTooltipPattern: '<b>${entityName}</b><br/><br/><b>TimeStamp:</b> ${ts:7}',
  circleTooltipFunction: null,
  circleFillColor: '#3388ff',
  circleFillColorOpacity: 0.2,
  useCircleFillColorFunction: false,
  circleFillColorFunction: null,
  circleStrokeColor: '#3388ff',
  circleStrokeOpacity: 1,
  circleStrokeWeight: 3,
  useCircleStrokeColorFunction: false,
  circleStrokeColorFunction: null
};

export enum PolylineDecoratorSymbol {
  arrowHead = 'arrowHead',
  dash = 'dash'
}

export const polylineDecoratorSymbolTranslationMap = new Map<PolylineDecoratorSymbol, string>(
  [
    [PolylineDecoratorSymbol.arrowHead, 'widgets.maps.decorator-symbol-arrow-head'],
    [PolylineDecoratorSymbol.dash, 'widgets.maps.decorator-symbol-dash']
  ]
);

export interface PolylineSettings {
  useStrokeWeightFunction?: boolean;
  strokeWeight: number;
  strokeWeightFunction?: string;
  useStrokeOpacityFunction?: boolean;
  strokeOpacity: number;
  strokeOpacityFunction?: string;
  useColorFunction?: boolean;
  color?: string;
  colorFunction?: string;
  usePolylineDecorator?: boolean;
  decoratorSymbol?: PolylineDecoratorSymbol;
  decoratorSymbolSize?: number;
  useDecoratorCustomColor?: boolean;
  decoratorCustomColor?: string;
  decoratorOffset?: string;
  endDecoratorOffset?: string;
  decoratorRepeat?: string;
}

export interface WidgetPolylineSettings extends PolylineSettings {
  parsedColorFunction: GenericFunction;
  parsedStrokeOpacityFunction: GenericFunction;
  parsedStrokeWeightFunction: GenericFunction;
}

export const defaultRouteMapSettings: PolylineSettings = {
  strokeWeight: 2,
  strokeOpacity: 1.0
};

export const defaultTripAnimationPathSettings: PolylineSettings = {
  color: null,
  strokeWeight: 2,
  strokeOpacity: 1,
  useColorFunction: false,
  colorFunction: null,
  usePolylineDecorator: false,
  decoratorSymbol: PolylineDecoratorSymbol.arrowHead,
  decoratorSymbolSize: 10,
  useDecoratorCustomColor: false,
  decoratorCustomColor: '#000',
  decoratorOffset: '20px',
  endDecoratorOffset: '20px',
  decoratorRepeat: '20px'
};

export interface PointsSettings {
  showPoints?: boolean;
  pointColor?: string;
  useColorPointFunction?: false;
  colorPointFunction?: string;
  pointSize?: number;
  usePointAsAnchor?: false;
  pointAsAnchorFunction?: string;
  pointTooltipOnRightPanel?: boolean;
}

export interface WidgetPointsSettings extends PointsSettings {
  parsedColorPointFunction: GenericFunction;
  parsedPointAsAnchorFunction: GenericFunction;
}

export const defaultTripAnimationPointSettings: PointsSettings = {
  showPoints: false,
  pointColor: null,
  useColorPointFunction: false,
  colorPointFunction: null,
  pointSize: 10,
  usePointAsAnchor: false,
  pointAsAnchorFunction: null,
  pointTooltipOnRightPanel: true
};

export interface MarkerClusteringSettings {
  useClusterMarkers: boolean;
  zoomOnClick: boolean;
  maxZoom: number;
  maxClusterRadius: number;
  animate: boolean;
  spiderfyOnMaxZoom: boolean;
  showCoverageOnHover: boolean;
  chunkedLoading: boolean;
  removeOutsideVisibleBounds: boolean;
  useIconCreateFunction: boolean;
  clusterMarkerFunction?: string;
}

export interface WidgetMarkerClusteringSettings extends MarkerClusteringSettings {
  parsedClusterMarkerFunction?: GenericFunction;
}

export const defaultMarkerClusteringSettings: MarkerClusteringSettings = {
  useClusterMarkers: false,
  zoomOnClick: true,
  maxZoom: null,
  maxClusterRadius: 80,
  animate: true,
  spiderfyOnMaxZoom: false,
  showCoverageOnHover: true,
  chunkedLoading: false,
  removeOutsideVisibleBounds: true,
  useIconCreateFunction: false,
  clusterMarkerFunction: null
};

export interface MapEditorSettings {
  snappable: boolean;
  initDragMode: boolean;
  hideAllControlButton: boolean;
  hideDrawControlButton: boolean;
  hideEditControlButton: boolean;
  hideRemoveControlButton: boolean;
}

export const defaultMapEditorSettings: MapEditorSettings = {
  snappable: false,
  initDragMode: false,
  hideAllControlButton: false,
  hideDrawControlButton: false,
  hideEditControlButton: false,
  hideRemoveControlButton: false
};

export type UnitedMapSettings = MapProviderSettings & CommonMapSettings & MarkersSettings &
  PolygonSettings & CircleSettings & PolylineSettings & PointsSettings & WidgetMarkerClusteringSettings & MapEditorSettings;

export const defaultMapSettings: UnitedMapSettings = {
  ...defaultMapProviderSettings,
  ...defaultCommonMapSettings,
  ...defaultMarkersSettings,
  ...defaultPolygonSettings,
  ...defaultCircleSettings,
  ...defaultRouteMapSettings,
  ...defaultMarkerClusteringSettings,
  ...defaultMapEditorSettings
};

export type WidgetUnitedMapSettings = UnitedMapSettings & Partial<WidgetCommonMapSettings &
  WidgetMarkersSettings & WidgetPolygonSettings & WidgetCircleSettings & WidgetPolylineSettings & WidgetPointsSettings>;

export type UnitedTripAnimationSettings = MapProviderSettings & TripAnimationCommonSettings & TripAnimationMarkerSettings &
  PolylineSettings & PointsSettings & PolygonSettings & CircleSettings;

export const defaultTripAnimationSettings: UnitedTripAnimationSettings = {
  ...defaultMapProviderSettings,
  ...defaultTripAnimationCommonSettings,
  ...defaultTripAnimationMarkersSettings,
  ...defaultTripAnimationPathSettings,
  ...defaultTripAnimationPointSettings,
  ...defaultPolygonSettings,
  ...defaultCircleSettings,
};

export interface HistorySelectSettings {
  buttonColor: string;
}

export type WidgetUnitedTripAnimationSettings = UnitedTripAnimationSettings & HistorySelectSettings &
  Partial<WidgetTripAnimationCommonSettings & WidgetTripAnimationMarkerSettings &
          WidgetPolylineSettings & WidgetPointsSettings & WidgetPolygonSettings & WidgetCircleSettings>;

