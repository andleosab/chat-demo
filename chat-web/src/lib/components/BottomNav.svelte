<script lang="ts">
  import { page } from '$app/state';
  import MessageSquareIcon from '@lucide/svelte/icons/message-square';
  import UsersIcon from '@lucide/svelte/icons/users';
  import UsersRoundIcon from '@lucide/svelte/icons/users-round';
  import LogOutIcon from '@lucide/svelte/icons/log-out';
  import * as Avatar from '$lib/components/ui/avatar/index.js';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu/index.js';
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

<nav class="md:hidden flex items-stretch h-16 border-t bg-background/95 backdrop-blur-sm shrink-0">

  {#each navItems as item}
    {@const active = page.url.pathname.startsWith(item.url)}
    <a
      href={item.url}
      class="flex flex-col items-center justify-center gap-0.5 flex-1 transition-colors
        {active ? 'text-primary' : 'text-muted-foreground hover:text-foreground'}"
    >
      <item.icon class="size-5 {active ? 'stroke-[2.5]' : 'stroke-2'}" />
      <span class="text-[10px] font-medium">{item.title}</span>
    </a>
  {/each}

  <!-- Profile / Me tab -->
  <DropdownMenu.Root>
    <DropdownMenu.Trigger>
      {#snippet child({ props })}
        <button
          {...props}
          class="flex flex-col items-center justify-center gap-0.5 flex-1 text-muted-foreground hover:text-foreground transition-colors"
        >
          <div class="relative">
            <Avatar.Root class="size-6 rounded-md">
              <Avatar.Fallback class="rounded-md text-[10px] bg-primary/10 text-primary font-medium">
                {initials(user.username)}
              </Avatar.Fallback>
            </Avatar.Root>
            <span
              class="absolute -bottom-0.5 -right-0.5 size-2 rounded-full border-2 border-background
                {isConnected ? 'bg-green-500' : 'bg-red-400'}"
            ></span>
          </div>
          <span class="text-[10px] font-medium">Me</span>
        </button>
      {/snippet}
    </DropdownMenu.Trigger>
    <DropdownMenu.Content side="top" align="end" class="w-48 mb-1">
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

</nav>
