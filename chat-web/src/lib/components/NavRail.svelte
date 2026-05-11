<script lang="ts">
  import { page } from '$app/state';
  import MessageSquareIcon from '@lucide/svelte/icons/message-square';
  import UsersIcon from '@lucide/svelte/icons/users';
  import UsersRoundIcon from '@lucide/svelte/icons/users-round';
  import LogOutIcon from '@lucide/svelte/icons/log-out';
  import * as Avatar from '$lib/components/ui/avatar/index.js';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu/index.js';
  import * as Tooltip from '$lib/components/ui/tooltip/index.js';
  import type { CurrentUser } from '$lib/store/user';
  import { client } from '$lib/auth-client';
  import { goto } from '$app/navigation';

  let { user, isConnected }: { user: CurrentUser; isConnected: boolean } = $props();

  const navItems = [
    { title: 'Chats',  url: '/chats',  icon: MessageSquareIcon },
    { title: 'Users',  url: '/users',  icon: UsersIcon },
    { title: 'Groups', url: '/groups', icon: UsersRoundIcon },
  ];

  function initials(name: string) {
    return name.slice(0, 2).toUpperCase();
  }
</script>

<nav class="hidden md:flex flex-col items-center w-14 h-full border-r bg-background py-3 shrink-0 gap-1">

  <!-- App logo -->
  <a
    href="/chats"
    class="flex items-center justify-center size-9 rounded-xl mb-2 text-primary hover:bg-primary/10 transition-colors"
  >
    <MessageSquareIcon class="size-5" />
  </a>

  <!-- Nav items -->
  <div class="flex flex-col gap-1 flex-1 w-full px-2">
    {#each navItems as item}
      {@const active = page.url.pathname.startsWith(item.url)}
      <Tooltip.Root>
        <Tooltip.Trigger>
          {#snippet child({ props })}
            <a
              href={item.url}
              {...props}
              class="flex items-center justify-center size-10 w-full rounded-xl transition-colors
                {active
                  ? 'bg-primary text-primary-foreground shadow-sm'
                  : 'text-muted-foreground hover:bg-muted hover:text-foreground'}"
            >
              <item.icon class="size-4" />
            </a>
          {/snippet}
        </Tooltip.Trigger>
        <Tooltip.Content side="right">
          {item.title}
        </Tooltip.Content>
      </Tooltip.Root>
    {/each}
  </div>

  <!-- User avatar + sign out -->
  <div class="px-2 w-full">
    <DropdownMenu.Root>
      <DropdownMenu.Trigger>
        {#snippet child({ props })}
          <button
            {...props}
            class="relative flex items-center justify-center size-10 w-full rounded-xl hover:bg-muted transition-colors"
          >
            <Avatar.Root class="size-7 rounded-lg">
              <Avatar.Fallback class="rounded-lg text-xs bg-primary/10 text-primary font-medium">
                {initials(user.username)}
              </Avatar.Fallback>
            </Avatar.Root>
            <!-- Connection dot -->
            <span
              class="absolute bottom-1.5 right-1.5 size-2 rounded-full border-2 border-background
                {isConnected ? 'bg-green-500' : 'bg-red-400'}"
            ></span>
          </button>
        {/snippet}
      </DropdownMenu.Trigger>
      <DropdownMenu.Content side="right" align="end" class="w-48">
        <div class="px-2 py-2">
          <p class="text-sm font-medium truncate">{user.username}</p>
          <p class="text-xs text-muted-foreground flex items-center gap-1.5 mt-0.5">
            <span class="size-1.5 rounded-full {isConnected ? 'bg-green-500' : 'bg-red-400'}"></span>
            {isConnected ? 'Online' : 'Offline'}
          </p>
        </div>
        <DropdownMenu.Separator />
        <DropdownMenu.Item
          onSelect={async () => {
            await client.signOut();
            goto('/sign-in');
          }}
        >
          <LogOutIcon class="size-4" />
          Sign out
        </DropdownMenu.Item>
      </DropdownMenu.Content>
    </DropdownMenu.Root>
  </div>

</nav>
