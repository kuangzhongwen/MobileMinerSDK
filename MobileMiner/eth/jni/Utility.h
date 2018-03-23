
#pragma once

#ifndef C_UTILITY_H
#define C_UTILITY_H

#include <sstream>

template <typename T> std::string to_string(T value) {
    // create an output string stream
    std::ostringstream os ;
    // throw the value into the string stream
    os << value ;
    // convert the string stream into a string and return
    return os.str() ;
}

#endif