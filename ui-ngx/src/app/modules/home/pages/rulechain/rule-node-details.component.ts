

import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { PageComponent } from '@shared/components/page.component';
import { Store } from '@ngrx/store';
import { AppState } from '@core/core.state';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { FcRuleNode, RuleNodeType } from '@shared/models/rule-node.models';
import { EntityType } from '@shared/models/entity-type.models';
import { Subject } from 'rxjs';
import { RuleNodeConfigComponent } from './rule-node-config.component';
import { Router } from '@angular/router';
import { RuleChainType } from '@app/shared/models/rule-chain.models';
import { ComponentClusteringMode } from '@shared/models/component-descriptor.models';
import { coerceBoolean } from '@shared/decorators/coercion';
import { ServiceType } from '@shared/models/queue.models';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'tb-rule-node',
  templateUrl: './rule-node-details.component.html',
  styleUrls: ['./rule-node-details.component.scss']
})
export class RuleNodeDetailsComponent extends PageComponent implements OnInit, OnChanges, OnDestroy {

  @ViewChild('ruleNodeConfigComponent') ruleNodeConfigComponent: RuleNodeConfigComponent;

  @Input()
  ruleNode: FcRuleNode;

  @Input()
  ruleChainId: string;

  @Input()
  ruleChainType: RuleChainType;

  @Input()
  @coerceBoolean()
  disabled = false;

  @Input()
  @coerceBoolean()
  isAdd = false;

  @Output()
  initRuleNode = new EventEmitter<void>();

  @Output()
  changeScript = new EventEmitter<void>();

  ruleNodeType = RuleNodeType;
  entityType = EntityType;

  serviceType = ServiceType.TB_RULE_ENGINE;

  ruleNodeFormGroup: UntypedFormGroup;

  private destroy$ = new Subject<void>();

  constructor(protected store: Store<AppState>,
              private fb: UntypedFormBuilder,
              private router: Router) {
    super(store);
    this.ruleNodeFormGroup = this.fb.group({});
  }

  private buildForm() {
    if (this.ruleNode) {
      this.ruleNodeFormGroup = this.fb.group({
        name: [this.ruleNode.name, [Validators.required, Validators.pattern('(.|\\s)*\\S(.|\\s)*'), Validators.maxLength(255)]],
        debugMode: [this.ruleNode.debugMode, []],
        singletonMode: [this.ruleNode.singletonMode, []],
        configuration: [this.ruleNode.configuration, [Validators.required]],
        additionalInfo: this.fb.group(
          {
            description: [this.ruleNode.additionalInfo ? this.ruleNode.additionalInfo.description : ''],
          }
        )
      });

      if (this.isAddQueue()) {
        this.ruleNodeFormGroup.addControl('queueName', this.fb.control(this.ruleNode?.queueName ? this.ruleNode.queueName : null));
        if (this.isSingleton()) {
          if (!this.isSingletonEditAllowed()) {
            this.ruleNodeFormGroup.get('singletonMode').disable({emitEvent: false});
          }
          if (!this.ruleNodeFormGroup.get('singletonMode').value) {
            this.ruleNodeFormGroup.get('queueName').disable({emitEvent: false});
          }
          this.ruleNodeFormGroup.get('singletonMode').valueChanges.pipe(
            takeUntil(this.destroy$)
          ).subscribe(value => {
            if (value) {
              this.ruleNodeFormGroup.get('queueName').enable({emitEvent: false});
            } else {
              this.ruleNodeFormGroup.get('queueName').disable({emitEvent: false});
            }
          });
        }
      }

      this.ruleNodeFormGroup.valueChanges.pipe(
        takeUntil(this.destroy$)
      ).subscribe(() => this.updateRuleNode());

    } else {
      this.ruleNodeFormGroup = this.fb.group({});
    }
    if (this.disabled) {
      this.ruleNodeFormGroup.disable({emitEvent: false});
    }
  }

  private updateRuleNode() {
    const formValue = this.ruleNodeFormGroup.value || {};
    formValue.name = formValue.name.trim();
    Object.assign(this.ruleNode, formValue);
  }

  ngOnInit(): void {
    if (this.disabled) {
      this.ruleNodeFormGroup.disable({emitEvent: false});
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngOnChanges(changes: SimpleChanges): void {
    for (const propName of Object.keys(changes)) {
      const change = changes[propName];
      if (change.currentValue !== change.previousValue) {
        if (propName === 'ruleNode') {
          this.buildForm();
        }
      }
    }
  }

  validate() {
    this.ruleNodeConfigComponent.validate();
  }

  openRuleChain($event: Event) {
    if ($event) {
      $event.stopPropagation();
    }
    const ruleChainId = this.ruleNodeFormGroup.get('configuration')?.value?.ruleChainId;
    if (ruleChainId) {
      if (this.ruleChainType === RuleChainType.EDGE) {
        this.router.navigateByUrl(`/edgeManagement/ruleChains/${ruleChainId}`);
      } else {
        this.router.navigateByUrl(`/ruleChains/${ruleChainId}`);
      }
    }
  }

  isAddQueue() {
    return this.isSingleton() || this.ruleNode.component.hasQueueName;
  }

  isSingleton() {
    return this.ruleNode.component.clusteringMode === ComponentClusteringMode.SINGLETON ||
      this.ruleNode.component.clusteringMode === ComponentClusteringMode.USER_PREFERENCE;
  }

  isSingletonEditAllowed() {
    return this.ruleNode.component.clusteringMode === ComponentClusteringMode.USER_PREFERENCE;
  }
}
