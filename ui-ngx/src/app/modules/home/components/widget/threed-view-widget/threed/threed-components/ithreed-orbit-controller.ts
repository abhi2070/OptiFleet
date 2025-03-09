import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';

export interface IThreedOrbitController {
    getOrbitController(): OrbitControls;
}
