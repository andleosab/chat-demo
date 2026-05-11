<script lang="ts">
  import type { PageProps } from './$types';
  import { Button } from '$lib/components/ui/button/index.js';
  import UsersRoundIcon from '@lucide/svelte/icons/users-round';
  import PlusIcon from '@lucide/svelte/icons/plus';
  import MessageSquareIcon from '@lucide/svelte/icons/message-square';
  import * as Avatar from '$lib/components/ui/avatar/index.js';

  let { data } = $props() as PageProps;
  let groups = $derived(data.groups);

  function initials(name: string) {
    return name.slice(0, 2).toUpperCase();
  }
</script>

<div class="flex flex-col h-full min-h-0">
  <header class="border-b px-4 py-3.5 shrink-0 flex items-center justify-between">
    <h1 class="text-base font-semibold">Groups</h1>
    <Button size="sm" href="/groups/new" class="gap-1.5 h-8 rounded-lg">
      <PlusIcon class="size-3.5" />
      New Group
    </Button>
  </header>

  <section class="flex-1 overflow-y-auto min-h-0">
    {#if groups.length === 0}
      <div class="flex flex-col items-center justify-center h-full gap-3 text-muted-foreground">
        <div class="size-14 rounded-2xl bg-muted flex items-center justify-center">
          <UsersRoundIcon class="size-6 opacity-50" />
        </div>
        <div class="text-center">
          <p class="text-sm font-medium text-foreground">No groups yet</p>
          <p class="text-xs mt-0.5">Create one to start a group conversation</p>
        </div>
      </div>
    {:else}
      <ul class="divide-y divide-border/60">
        {#each groups as group (group.conversation_id)}
          <li class="flex items-center gap-3 px-4 py-3 hover:bg-muted/50 transition-colors">
            <Avatar.Root class="size-10 rounded-xl shrink-0">
              <Avatar.Fallback class="rounded-xl text-sm font-medium bg-primary/10 text-primary">
                {initials(group.name ?? '?')}
              </Avatar.Fallback>
            </Avatar.Root>

            <a
              href="/groups/{group.conversation_id}/{encodeURIComponent(group.name ?? '')}"
              class="flex-1 min-w-0"
            >
              <p class="font-medium text-sm truncate">{group.name ?? 'Unnamed'}</p>
              <p class="text-xs text-muted-foreground">Group chat</p>
            </a>

            <Button
              href="/chats/{group.conversation_id}/{encodeURIComponent(group.name ?? '')}"
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
