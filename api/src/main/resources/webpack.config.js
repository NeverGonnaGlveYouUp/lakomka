const path = require("path");
   
module.exports = {
    mode: "development",
    entry: "./app/app.jsx",
    output:{
        path: path.resolve(__dirname, "./static"),
        publicPath: "/static/",
        filename: "bundle.js"
    },
    devServer: {
     historyApiFallback: true,
     static: {
      directory: path.join(__dirname, "/"),
     }
    },
    module:{
        rules:[
            {
                test: /\.css$/,
                use: ["style-loader", "css-loader"]
            },
            {
                test: /\.jsx?$/,
                exclude: /(node_modules)/,
                loader: "babel-loader",
                options:{
                    presets:["@babel/preset-react", "@babel/preset-env"]
                }
            }
        ]
    }
}