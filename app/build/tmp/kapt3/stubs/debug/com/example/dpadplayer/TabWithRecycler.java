package com.example.dpadplayer;

/**
 * Implement on tab fragments that host a RecyclerView so LibraryFragment can move focus into them.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\n\u0010\u0002\u001a\u0004\u0018\u00010\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H\u0016\u00a8\u0006\u0006"}, d2 = {"Lcom/example/dpadplayer/TabWithRecycler;", "", "recyclerView", "Landroidx/recyclerview/widget/RecyclerView;", "requestInitialFocus", "", "app_debug"})
public abstract interface TabWithRecycler {
    
    @org.jetbrains.annotations.Nullable
    public abstract androidx.recyclerview.widget.RecyclerView recyclerView();
    
    /**
     * Request initial focus into the recycler (first focusable child).
     */
    public abstract void requestInitialFocus();
    
    /**
     * Implement on tab fragments that host a RecyclerView so LibraryFragment can move focus into them.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
        
        /**
         * Request initial focus into the recycler (first focusable child).
         */
        public static void requestInitialFocus(@org.jetbrains.annotations.NotNull
        com.example.dpadplayer.TabWithRecycler $this) {
        }
    }
}