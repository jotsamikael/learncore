import {
  Directive,
  Input,
  OnDestroy,
  TemplateRef,
  ViewContainerRef,
} from '@angular/core';
import { Subscription } from 'rxjs';
import { PermissionService } from '../services/permission.service';
import { SessionService } from '../services/session.service';

@Directive({
  selector: '[hasPermission]',
  standalone: false,
})
export class HasPermissionDirective implements OnDestroy {

  private subscription?: Subscription;
  private permissions: string[] = [];
  private mode: 'any' | 'all' = 'any';

  @Input()
  set hasPermission(value: string | string[]) {
    this.permissions = Array.isArray(value) ? value : [value];
    this.updateView();
  }

  @Input()
  set hasPermissionMode(mode: 'any' | 'all') {
    this.mode = mode;
    this.updateView();
  }

  constructor(
    private readonly templateRef: TemplateRef<unknown>,
    private readonly viewContainer: ViewContainerRef,
    private readonly permissionService: PermissionService,
    private readonly session: SessionService
  ) {
    this.subscription = this.session.session$.subscribe(() => this.updateView());
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  private updateView(): void {
    const allowed = this.mode === 'all'
      ? this.permissionService.canAll(this.permissions)
      : this.permissionService.canAny(this.permissions);

    this.viewContainer.clear();
    if (allowed) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    }
  }
}
