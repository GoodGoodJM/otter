package io.github.goodgoodjm.otter

import org.springframework.boot.sql.init.dependency.AbstractBeansOfTypeDatabaseInitializerDetector
import java.util.*

class OtterDatabaseInitializerDetector : AbstractBeansOfTypeDatabaseInitializerDetector() {
    override fun getDatabaseInitializerBeanTypes(): MutableSet<Class<*>> =
        Collections.singleton(SpringOtter::class.java)
}