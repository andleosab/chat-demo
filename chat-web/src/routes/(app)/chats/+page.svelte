<script lang="ts">
  import type { PageProps } from './$types';
  import MessageSquareIcon from '@lucide/svelte/icons/message-square';
  import type { ConversationResponse } from '$lib/api/types/conversation';
  import * as Avatar from '$lib/components/ui/avatar/index.js';

  let { data } = $props() as PageProps;
  let conversations = $derived(data.conversations as ConversationResponse[]);

  function formatDate(date: Date | string | null | undefined) {
    if (!date) return '';
    const d = new Date(date);
    const now = new Date();
    const isToday = d.toDateString() === now.toDateString();
    if (isToday) {
      return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
    return d.toLocaleDateString([], { month: 'short', day: 'numeric' });
  }

  function initials(name: string) {
    return name
      .split(' ')
      .map(w => w[0])
      .join('')
      .slice(0, 2)
      .toUpperCase();
  }
</script>

<div class="flex flex-col h-full min-h-0">
  <header class="border-b px-4 py-3.5 shrink-0">
    <h1 class="text-base font-semibold">Chats</h1>
  </header>

  <section class="flex-1 overflow-y-auto min-h-0">
    {#if conversations.length === 0}
      <div class="flex flex-col items-center justify-center h-full gap-3 text-muted-foreground">
        <div class="size-14 rounded-2xl bg-muted flex items-center justify-center">
          <MessageSquareIcon class="size-6 opacity-50" />
        </div>
        <div class="text-center">
          <p class="text-sm font-medium text-foreground">No conversations yet</p>
          <p class="text-xs mt-0.5">Start a chat from the Users tab</p>
        </div>
      </div>

    {:else}
      <ul class="divide-y divide-border/60">
        {#each conversations as conversation (conversation.conversation_id)}
          <li>
            <a
              href="/chats/{conversation.conversation_id}/{encodeURIComponent(conversation.name ?? '')}"
              class="flex items-center gap-3 px-4 py-3 hover:bg-muted/50 transition-colors"
            >
              <Avatar.Root class="size-10 rounded-xl shrink-0">
                <Avatar.Fallback class="rounded-xl text-sm font-medium bg-primary/10 text-primary">
                  {initials(conversation.name ?? '?')}
                </Avatar.Fallback>
              </Avatar.Root>

              <div class="flex-1 min-w-0">
                <p class="font-medium text-sm truncate">{conversation.name ?? 'Unnamed'}</p>
                <p class="text-xs text-muted-foreground mt-0.5">
                  {conversation.type === 'group' ? 'Group chat' : 'Direct message'}
                </p>
              </div>

              {#if conversation.last_message_at}
                <span class="text-[11px] text-muted-foreground shrink-0">
                  {formatDate(conversation.last_message_at)}
                </span>
              {/if}
            </a>
          </li>
        {/each}
      </ul>
    {/if}
  </section>
</div>
