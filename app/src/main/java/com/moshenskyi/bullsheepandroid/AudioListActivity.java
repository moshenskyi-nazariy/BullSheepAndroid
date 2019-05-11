package com.moshenskyi.bullsheepandroid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioListActivity extends AppCompatActivity {


    private RecyclerView bookListRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        bookListRv = findViewById(R.id.bookListRv);
        bookListRv.setLayoutManager(new GridLayoutManager(this, 2));
        initRv();
    }


    private void initRv() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(null,"Money Something", 50));
        books.add(new Book(null,"Money Something", 50));
        books.add(new Book(null,"Money Something", 40));
        books.add(new Book(null,null, 20));

        bookListRv.setAdapter(new BookAdapter(this, books ));
    }




    class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
        private Context context;
        private List<Book> bookList;

        public BookAdapter(Context context, List<Book> bookList) {
            this.context = context;
            this.bookList = bookList;
        }

        @NonNull
        @Override
        public BookViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = View.inflate(context, R.layout.book_item,null);
            return new BookViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BookViewHolder bookViewHolder, int i) {

            bookViewHolder.progress.setProgress(bookList.get(i).progress);
            if (bookList.get(i).title != null) {
                bookViewHolder.title.setText(bookList.get(i).title);
            }

            if (bookList.get(i).photoLink != null) {
                Glide.with(context)
                        .load(new File(bookList.get(i).photoLink))
                        .into(bookViewHolder.photoIv);
            }
        }

        @Override
        public int getItemCount() {
            return bookList.size();
        }

        class BookViewHolder extends RecyclerView.ViewHolder {

            private TextView title;
            private ImageView photoIv;
            private ProgressBar progress;

            public BookViewHolder(@NonNull View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.bookItemTitle);
                photoIv = itemView.findViewById(R.id.bookImage);
                progress = itemView.findViewById(R.id.bookItemProgressBar);

            }
        }

    }
}
