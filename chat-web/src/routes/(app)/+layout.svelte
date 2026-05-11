<script lang="ts">
  import { onMount, onDestroy } from 'svelte';
  import { connect, isConnected, disconnect } from '$lib/store/ws';
  import { currentUser } from '$lib/store/user';
  import { userSession } from '$lib/state/user-session.svelte';
  import * as Tooltip from '$lib/components/ui/tooltip/index.js';
  import NavRail from '$lib/components/NavRail.svelte';
  import BottomNav from '$lib/components/BottomNav.svelte';

  let { data, children } = $props();

  onMount(() => {
    currentUser.set(data.currentUser);
    connect($currentUser!.userId);
    userSession.currentUser = data.currentUser;
  });

  onDestroy(() => {
    disconnect();
  });
</script>

<Tooltip.Provider delayDuration={200}>
  <div class="flex h-svh overflow-hidden bg-background">
    <!-- Desktop: icon nav rail -->
    <NavRail user={data.currentUser!} isConnected={$isConnected} />

    <!-- Content column -->
    <div class="flex flex-col flex-1 min-w-0 overflow-hidden">
      <main class="flex-1 overflow-hidden min-h-0">
        {@render children()}
      </main>
      <!-- Mobile: bottom tab bar -->
      <BottomNav user={data.currentUser!} isConnected={$isConnected} />
    </div>
  </div>
</Tooltip.Provider>
