import React, { useEffect, useState } from 'react';
import { FaRegUserCircle, FaRegEye } from "react-icons/fa";
import { FaRegHeart } from "react-icons/fa";
import { IoCartOutline } from "react-icons/io5";


class Header extends React.Component {

    render() {
        return(
            <header>
                <div className="containerNew">
                    <div className="header">
                        <div className="icons-container">
                             <FaRegUserCircle className="icon"/>
                             <FaRegEye className="icon"/>
                             <FaRegHeart className="icon"/>
                             <IoCartOutline className="icon"/>
                        </div>
                    </div>
                </div>
            </header>
        );
    }
}

export default Header;
