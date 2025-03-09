import { Subscription } from 'rxjs';
import { IThreedSceneManager } from '../threed-managers/ithreed-scene-manager';
import { IThreedComponent } from './ithreed-component';

export abstract class ThreedBaseComponent implements IThreedComponent {

    protected sceneManager: IThreedSceneManager;
    protected initialized = false;
    protected subscriptions: Subscription[] = [];

    initialize(sceneManager: IThreedSceneManager): void {
        if(this.initialized) {return;}
        this.sceneManager = sceneManager;
        this.initialized = true;
    }

    tick(): void { }
    render(): void { }
    resize(): void { }
    onDestroy(): void {
        this.subscriptions.forEach(s => s.unsubscribe());
    }
}
