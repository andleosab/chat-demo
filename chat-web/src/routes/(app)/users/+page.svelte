<!-- src/routes/(app)/users/+page.svelte -->
<script lang="ts">
  import type { PageProps } from './$types';
  import type { CreateUserResponse } from '$lib/api/types/user';
  import * as Avatar from '$lib/components/ui/avatar/index.js';
  import { Button } from '$lib/components/ui/button/index.js';
  import MessageSquareIcon from '@lucide/svelte/icons/message-square';
  import UsersIcon from '@lucide/svelte/icons/users';
  import { userCreated } from '$lib/store/ws';

  let { data } = $props() as PageProps;
  let wsUsers = $state<CreateUserResponse[]>([]);
  let users = $derived([...(data.users ?? []), ...wsUsers]);

  $effect(() => {
    const unsubscribe = userCreated.subscribe(newUser => {
      if (newUser && !users.some(u => u.userId === newUser.userId)) {
        wsUsers = [...wsUsers, {
          userId: newUser.userId,
          username: newUser.username,
          email: newUser.email,
          isActive: true,
          createdAt: new Date(newUser.timestamp).toISOString(),
          updatedAt: ''
        }];
      }
    });

    return unsubscribe;
  });

  function initials(name: string) {
    return name.slice(0, 2).toUpperCase();
  }
</script>

<div class="flex flex-col h-full min-h-0">
  <header class="border-b px-4 py-3.5 shrink-0">
    <h1 class="text-base font-semibold">Users</h1>
  </header>

  <section class="flex-1 overflow-y-auto min-h-0">
    {#if users.length === 0}
      <div class="flex flex-col items-center justify-center h-full gap-3 text-muted-foreground">
        <div class="size-14 rounded-2xl bg-muted flex items-center justify-center">
          <UsersIcon class="size-6 opacity-50" />
        </div>
        <div class="text-center">
          <p class="text-sm font-medium text-foreground">No users yet</p>
          <p class="text-xs mt-0.5">Users will appear here when they sign up</p>
        </div>
      </div>
    {:else}
      <ul class="divide-y divide-border/60">
        {#each users as user (user.userId)}
          <li class="flex items-center gap-3 px-4 py-3 hover:bg-muted/50 transition-colors">
            <Avatar.Root class="size-10 rounded-xl shrink-0">
              <Avatar.Fallback class="rounded-xl text-sm font-medium bg-primary/10 text-primary">
                {initials(user.username)}
              </Avatar.Fallback>
            </Avatar.Root>

            <div class="flex-1 min-w-0">
              <p class="font-medium text-sm truncate">{user.username}</p>
              <p class="text-xs text-muted-foreground truncate">{user.email}</p>
            </div>

            <Button
              href="/chats/new/{user.userId}/{user.username}"
              variant="ghost"
              size="icon-sm"
              class="shrink-0 rounded-lg text-muted-foreground hover:text-foreground"
            >
              <MessageSquareIcon class="size-4" />
            </Button>
          </li>
        {/each}
      </ul>
    {/if}
  </section>
</div>
