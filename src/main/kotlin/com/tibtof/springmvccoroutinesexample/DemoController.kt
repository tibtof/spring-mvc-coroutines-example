package com.tibtof.springmvccoroutinesexample

import kotlinx.coroutines.delay
import kotlinx.coroutines.future.future
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@RestController
@OptIn(ExperimentalTime::class)
class DemoController : SpringScope by SpringScope() {

    @GetMapping("/boomerang/{x}")
    fun demo(@PathVariable x: Int): CompletableFuture<Int> = future {
        if (x == 0) throw IllegalArgumentException("Zero not allowed")
        delay(x.seconds)
        x
    }
}