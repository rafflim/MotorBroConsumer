package com.elevintech.motorbro.Model

import java.io.Serializable

// INHERITS the SHOP class
class Branch(

    var branchId: String = ""

): Serializable, Shop()