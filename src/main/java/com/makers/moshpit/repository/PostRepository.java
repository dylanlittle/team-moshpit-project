package com.makers.moshpit.repository;

import com.makers.moshpit.model.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Long> {
}
